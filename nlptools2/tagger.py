#!/usr/bin/python
# -*- encoding: utf-8 -*-

"""
Снятие морфологической омонимии русского текста.
Статистический тэггер-лемматизатор для русского языка на основе библиотеки pymorphy2.
Использует статистику совместного употребления окончаний слов в тексте,
а также статистику зависимости падежей от управляющих предлогов.
Для Python 3
"""

import sys
import os
import time
import itertools
import re
import math
from random import choice
from datetime import datetime
from collections import defaultdict, OrderedDict
import struct

import dawg
import pymorphy2

from pickling import *
from tokenizer import Tokenizer
from segmentizer import Segmentizer
from dater import Dater, _U
import mc
from commontools import *
from morphotools import *


class Tagger(object):
    """
    Статистический тэггер-лемматизатор для русского языка на основе pymorphy
    """

    def __init__(self, morph=None, dater=None):
        """
        Инициализация тэггера. Создание регулярных выражений для лемматизации.
        Подгрузка словаря аббревиатур.
        Создание словарей месяцев, падежей и пр.

        morph - морфологический словарь pymorphy2
        """
        if not morph:
            raise ValueError("No morphoanalyzer found!")
        # Рег. выражения для лемматизации
        self.digit = re.compile("^\d+$")
        self.eng = re.compile("^\d*[a-zA-Z]+(?:-[a-zA-Z])?$", re.UNICODE)
        self.short = re.compile("^[A-ZА-ЯЁ][a-zа-яё]?$")

        # Рег. выражения для разбиения текста на предложения
        self.splitter = re.compile("[.?!]+")
        self.starter = re.compile("[А-ЯЁA-Z\d\"\'\(\)\[\]~`«s-]")
        self.bad_ender = re.compile("^[А-ЯЁа-яёA-Za-z][а-яёa-z]?$")
        self.gram_spl = re.compile("[,\s]+")

        # Рег. выражение для управления
        self.prepcase = re.compile("\d+:" + mc.PREP + "(?:\d+:(?:" +
                                   mc.ADJF + "|" + mc.PRTF + "|" + mc.PRCL + "|" +
                                   mc.CONJ + "|" + mc.ADVB + "))*\d+:(?:" + mc.ADJF +
                                   "|" + mc.NOUN + "|" + mc.NPRO + ")(?:\d+:" +
                                   mc.CONJ + "\d+:(?:" + mc.NOUN + "|" + mc.NPRO + "))?")
        self.positem = re.compile("\d+:[А-Я-]+")
        # Морфология
        self.morph = morph
        # Обработка дат
        self.dater = dater
        # Аббревиатуры
        self.abbrs = unpkl_2layered_s(os.path.join(os.path.dirname(sys.argv[0]), "dicts/abbr.pkl"))
        # Суффиксные частоты
        self.freqs = dawg.BytesDAWG()
        self.weights = defaultdict(float)
        self.small = 0.0

    def gram_bad(self, word):
        """
        Возвращает грамматические признаки для слова, которого нет в словаре pymorphy.

        Если self.dater != None, для всех дат в качестве части речи указывается "DD" "MM", "YY" или "YYYY",
        а в качестве граммем - формат YYYY-MM-DD, YY-MM-DD, YYYY-MM или MM-DD с суффиксом -B, -L, -I или -U
        """
        if self.dater:
            if self.dater.is_date(word):
                date = self.dater.check_date(word)
                if date:
                    return {mc._lemma: date[0], mc._pos: date[1], mc._gram: {date[1] + _U}}
                return {mc._lemma: word}
        if re.match(self.digit, word):
            return {mc._lemma: word, mc._pos: mc.NUMB}
        if re.match(self.eng, word):
            if word.endswith("s'") or word.endswith("'s"):
                return {mc._lemma: word, mc._pos: mc.ADJF}
            if word.endswith("'a") or word.endswith("'а"):
                word = word[:-2]
            return {mc._lemma: word, mc._pos: mc.NOUN}
        if word in self.abbrs.keys():
            return self.abbrs[word]
        return {mc._lemma: word}

    def check_lemma(self, norm, word):
        """
        Проверка того, что возвращается непустая лемма (если лемма пуста, вместо нее возвращается само слово)
        """
        lexeme = norm.normal_form.replace("ё", "е").replace("Ё", "Е")

        if not lexeme and "-" in word: # Для сложного слова пытаемся лемматизировать каждую из частей
            try:
                lexeme = "-".join([self.morph.parse(part)[0].normal_form if self.morph.parse(part)[0].normal_form else part
                               for part in lexeme.split("-")])
            except Exception:
                print("Unable to lemmatize: ", word, "\nPossible lemma:", lexeme)
                sys.exit()
        elif not lexeme:
            lexeme = word.replace("ё", "е").replace("Ё", "Е")
        grams = re.split(self.gram_spl, str(norm.tag))
        pos = grams[0]
        if pos == mc.LATN:
            pos = mc.NOUN
        if pos == mc.ROMN:
            pos = mc.NUMB
        return {mc._lemma: lexeme, mc._pos: pos, mc._gram: set(grams[1:])}

    def gram_first(self, word):
        """
        Возвращает для слова его ПЕРВУЮ лемму, часть речи и грамматические признаки в виде словаря

        Если self.dater != None, для всех дат в качестве части речи указывается "DD" "MM", "YY" или "YYYY",
        а в качестве граммем - формат YYYY-MM-DD, YY-MM-DD, YYYY-MM или MM-DD с суффиксом -B, -L, -I или -U
        """
        data = self.morph.parse(word)
        if data:
            return [self.check_lemma(data[0], word)]
        return [self.gram_bad(word)]

    def gram_all(self, word):
        """
        Возвращает для слова ВСЕ его леммы, части речи и грамматические признаки в виде кортежа словарей

        Если self.dater != None, для всех дат в качестве части речи указывается "DD" "MM", "YY" или "YYYY",
        а в качестве граммем - формат YYYY-MM-DD, YY-MM-DD, YYYY-MM или MM-DD с суффиксом -B, -L, -I или -U
        """
        data = self.morph.parse(word)
        if data:
            return [self.check_lemma(info, word) for info in data]
        return [self.gram_bad(word)]

    def lemm_list(self, word, true_lemma):
        """
        Возвращает список лемм слова, вначале - правильную лемму
        """
        norms = self.morph.parse(word)
        if norms:
            lemms = set([info.normal_form.replace("ё", "е").replace("Ё", "Ё") for info in norms])
            if len(lemms) == 1:
                if true_lemma in lemms:
                    return [true_lemma]
            if true_lemma in lemms:
                lemms.remove(true_lemma)
            return [true_lemma] + sorted(list(lemms))
        return [word]

    def get_sentence_cases(self, sentence):
        """
        Сбор статистики на основе падежей: обработка одного предложения (sentence)
        """
        if not sentence:
            return []
        result = []
        for (ind, info) in enumerate(sentence):
            if len(info) < 3:
                continue
            if not info[2].split("|")[0] in mc._declinable: # Работаем только со словами, которые могут иметь падеж
                continue
            norms = self.gram_all(info[0])  # Все возможные варианты лемм текущего слова
            try:
                true_cases = set(re.split(self.gram_spl, info[2].split("|")[1])).intersection(mc._cases)
                if len(true_cases) > 1:
                    continue
                true_case = true_cases.pop()
                all_vars = [norm for norm in norms if mc._gram in norm.keys()]
                all_cases = set([x for y in [norm[mc._gram].intersection(mc._cases)
                                             for norm in all_vars] for x in y])
                if not true_case in all_cases or len(all_cases) == 1:
                    continue
                prep = self.find_prep(sentence, ind)
                # Результат в виде <Ближайший слева предлог, Список возможных падежей, Правильный падеж>
                result.append("\t".join((prep, "|".join((sorted(all_cases))), true_case)))

            except Exception:
                continue

        return result

    def prepare_cases(self, trainfile):
        """
        Обработка тренировочного корпуса: убираем все, кроме предлогов и падежей,
        записываем в новый файл.
        """
        with open(trainfile, "r", encoding="UTF8") as fin, open(trainfile + ".cases", "w", encoding="UTF8") as fout:
            sentence = []
            for line in fin:
                if line == mc.BTAG: # Если это метка начала предложения
                    fout.write(line)
                    sentence = []
                    continue
                if line == mc.ETAG: # Если это метка конца предложения
                    case_result = self.get_sentence_cases(sentence)
                    if case_result:
                        fout.write("{0}\n{1}".format("\n".join(case_result), line))
                    else:
                        fout.write(line)
                    del sentence[:]
                    continue
                sentence.append(line.strip().split("\t"))
        return True

    def prepare_corpus(self, trainfile, suff_len):
        """
        Обработка тренировочного корпуса: убираем все, кроме суффиксов,
        записываем в новый файл.
        """
        with open(trainfile, "r", encoding="UTF8") as fin, open(trainfile + "." + str(suff_len).zfill(2) + ".suffs", "w", encoding="UTF8") as fout:
            for line in fin:
                if line in {mc.BTAG, mc.ETAG}: # Если это метка начала или конца предложения
                    fout.write(line)
                    continue
                items = line.strip().split("\t")
                if len(items) <= 2:
                    continue # Это знак препинания
                word = items[0].lower()
                lemms = [x.lower() for x in self.lemm_list(*items[:2])] # Список возможных лемм, первая - правильная
                suff = suffix(word, suff_len) # Трехбуквенный суффикс слова
                stem = longest_common([word] + lemms) # Наибольший общий префикс (стем?)
                lem_flexes = [suffix(lemma, len(lemma) - len(stem)) for lemma in lemms] # Берем только суффиксы от всех лемм
                fout.write("{0}\t{1}\n".format(suff, "\t".join(lem_flexes)))
        return True

    @staticmethod
    def count_sentence_suffs(sentence, freqs, cfreqs, radius):
        """
        Сбор статистики на основе суффиксов: обработка одного предложения
        """
        if not sentence:
            return True
        pairs = dict(enumerate(sentence))
        hom_nums = [num for (num, info) in pairs.items() if len(info) > 2] # Номера омонимов в предложении
        for hom_num in hom_nums:
            for num in smart_range(pairs.keys(), hom_num, radius):
                freqs[(num - hom_num, pairs[num][0], tuple(sorted(pairs[hom_num][1:])))][pairs[hom_num][1]] += 1
                cfreqs[(num - hom_num, tuple(sorted(pairs[hom_num][1:])))][pairs[hom_num][1]] += 1
        return True

    def find_prep(self, sentence, ind):
        """
        Нахождение ближайшего предлога слева от данного слова

        ind - номер данного слова в предложении sentence
        """
        sent = dict(enumerate(sentence))

        for cur in list(range(ind))[::-1]:
            if len(sent[cur]) < 3 and not re.match(self.splitter, sent[cur][0]):
                continue
            if not sent[cur][2] in mc._btwn_prep_noun:
                break
        if sent[cur][2] ==  mc.PREP:
            return sent[cur][1]
        return mc.NOPREP

    def count_sentence_cases(self, sentence, freqs):
        """
        Сбор статистики на основе падежей: обработка одного предложения (sentence)

        freqs - словарь для наполнения статистикой
        """
        if not sentence:
            return True
        for (ind, info) in enumerate(sentence):
            if len(info) < 3:
                continue
            if not info[2].split("|")[0] in mc._declinable: # Работаем только со словами, которые могут иметь падеж
                continue
            norms = self.gram_all(info[0])  # Все возможные варианты лемм текущего слова
            try:
                true_cases = set(re.split(self.gram_spl, info[2].split("|")[1])).intersection(mc._cases)
                if len(true_cases) > 1:
                    continue
                true_case = true_cases.pop()
                all_vars = [norm for norm in norms if mc._gram in norm.keys()]
                all_cases = set([x for y in [norm[mc._gram].intersection(mc._cases)
                                             for norm in all_vars]
                                for x in y])
                if not true_case in all_cases or len(all_cases) == 1:
                    continue
                prep = self.find_prep(sentence, ind)
                freqs[(prep, tuple(sorted(all_cases)))][true_case] += 1
            except Exception:
                continue

        return True

    def count_sentence_cases_re(self, sentence, freqs):
        """
        Сбор статистики на основе падежей: обработка одного предложения (sentence)
        с помощью регулярных выражений.
        Альтернативнвй способ: см. count_sentence_cases(self, sentence, freqs)

        freqs - словарь для наполнения статистикой
        """

        words = [(ind, info) for (ind, info) in enumerate(sentence) if len(info) > 2]
        words_pat = "".join(["{0:d}:{1}".format(ind, info[2].split("|")[0]) for (ind, info) in words])
        matches = re.findall(self.prepcase, words_pat)
        if matches == []:
            return True
        found = set()
        for match_obj in matches:
            pos_items = re.findall(self.positem, match_obj)
            inds = [int(x.split(":")[0]) for x in pos_items]
            found = found.union(set(inds))
            prep = sentence[inds[0]][1]

            for pos_item in pos_items[1:]:
                ind = int(pos_item.split(":")[0])
                if sentence[ind][2].split("|")[0] in mc._declinable:
                    self.add_case_counts(sentence[ind], freqs, prep)

        for (ind, info) in ((ind, info) for (ind, info) in words if info[2].split("|")[0] in mc._declinable and not ind in found):
            self.add_case_counts(info, freqs, mc.NOPREP)

        return True

    def add_case_counts(self, info, freqs, prep):
        norms = self.gram_all(info[0])  # Все возможные варианты лемм текущего слова
        try:
            true_cases = set(info[2].split("|")[1].split(",")).intersection(mc._cases)
            if len(true_cases) > 1:
                return True
            true_case = true_cases.pop()
            all_vars = [norm for norm in norms if mc._gram in norm.keys()]
            all_cases = set([x for y in [norm[mc._gram].intersection(mc._cases)
                                         for norm in all_vars] for x in y])
            if not true_case in all_cases or len(all_cases) == 1:
                return True
            freqs[(prep, tuple(sorted(all_cases)))][true_case] += 1
        except Exception:
            return True
        return True

    def train(self, trainfile, radius=2, suff_len=3):
        """
        Сбор статистики на основе суффиксов: обработка всего корпуса

        trainfile - размеченный корпус,
        radius - это радиус контекста, который учитывается при выбора правильной леммы,
        suff_len - длина суффиксов, на которых основано обучение
        """
        # Если тренировочный корпус еще не подготовлен, делаем это прямо сейчас
        if trainfile.endswith(".lemma"):
            Tagger.prepare_corpus(trainfile, suff_len)
            trainfile += "." + str(suff_len).zfill(2) + ".suffs"

        freqs = defaultdict(lambda: defaultdict(int))
        cfreqs = defaultdict(lambda: defaultdict(int))
        ranks = defaultdict(float)
        caseranks = defaultdict(float)

        # Структура словаря: {<Номер в контексте>, <Контекст>, <Список омонимов> : <Выбранный омоним> : <Вероятность>}
        normfreqs = defaultdict(lambda: defaultdict(float))
        # Структура словаря: {<Номер в контексте>, <Контекст>: <Ранг>}
        normweights = defaultdict(float)

        # Собираем частоты из корпуса
        with open(trainfile, "r", encoding="UTF8") as fin:
            sentence = []
            for line in fin:
                if line == mc.BTAG:
                    continue
                if line == mc.ETAG:
                    Tagger.count_sentence_suffs(sentence, freqs, cfreqs, radius)
                    del sentence[:]
                    sentence = []
                    continue
                sentence.append(line.strip().split("\t"))

        # Нормализуем частоты
        for k, v in freqs.items():
            total = sum([freq for freq in v.values()])
            for hom, freq in v.items():
                normfreqs[k][hom] = float(freq) / total

        # Вычисляем ранги контекстов
        for k, v in cfreqs.items():
            total = sum(v.values())
            entropy = - float(sum([freq * math.log(freq) for freq in v.values()]) - total * math.log(total)) / total
            ranks[k] = 1.0 / math.exp(entropy)

        # Вычисляем веса контекстов
        for k, v in ranks.items():
            normweights[k[0]] += v

        v_sum = sum([v for v in normweights.values()])
        for k, v in normweights.items():
            normweights[k] = v / v_sum

        # Сериализуем частоты и веса (ранги) контекстов (чем больше энтропия распределения по омонимам, тем меньше ранг (вес))

        dfreqs = dawg.BytesDAWG([("{0:d}\t{1}\t{2}\t{3}".format(k[0], k[1], " ".join(k[2]), hom), struct.pack("f", freq))
                                     for k, v in normfreqs.items() for hom, freq in v.items()])
        dfreqs.save(trainfile + ".freqs.dawg")
        dump_data(trainfile + ".weights.pkl", normweights)

        # Сериализуем small-значение (для тех случаев, которых нет в словаре)
        small = 1.0 / (2 * sum([freq for k, v in normfreqs.items() for v1, freq in v.items()]))
        dump_data(trainfile + ".small", small)

        return True

    def train_cases(self, trainfile, threshold=1, small_diff=0.2):
        """
        Обучение снятию падежной омонимии (автоматическое извлечение правил)

        trainfile - размеченный корпус,
        threshold - минимальная абсолютная частота вхождения правила в корпус,
        small_diff - максимальная допустимая разность между двумя вариантами правила с наибольшей вероятностью.
        """

        # Если тренировочный корпус еще не подготовлен, делаем это прямо сейчас
        if trainfile.endswith(".lemma"):
            self.prepare_cases(trainfile)
            trainfile += ".cases"

        freqs = defaultdict(lambda: defaultdict(int))
        self.caserules = defaultdict(str)

        # Собираем частоты из корпуса
        with open(trainfile, "r", encoding="UTF8") as fin:
            sentence = []
            for line in fin:
                if line == mc.BTAG:
                    continue
                if line == mc.ETAG:
                    for parts in sentence:
                        freqs[(parts[0], tuple(sorted(parts[1].split("|"))))][parts[-1]] += 1
                    del sentence[:]
                    sentence = []
                    continue
                sentence.append(line.strip().split("\t"))

        # Извлекаем правила
        for k, v in freqs.items():
            good_values = {case: freq for case, freq in v.items() if freq >= threshold}
            total = sum(good_values.values())
            for case, freq in good_values.items():
                freqs[k][case] = float(freq) / total
            chosen = argmax([(case, freq) for case, freq in good_values.items()])
            if chosen is None:
                continue
            if len(chosen) != 1:
                continue
            if len(v.keys()) == 1:
                self.caserules[k] = sorted(chosen)[0]
                continue
            second = argmax([(case, freq) for case, freq in good_values.items() if case != chosen[0]])
            if second:
                if freqs[k][chosen[0]] - freqs[k][second[0]] < small_diff:
                    continue
            self.caserules[k] = sorted(chosen)[0]

        # Тестовый вывод в файл
        #with open("prep_stat_new.txt", "w", encoding="UTF8") as fout:
        #    for k, v in sorted(freqs.items()):
        #        total = sum([freq for freq in v.values()])
        #        entropy = - sum([float(freq) * math.log(float(freq) / total) / total  for freq in v.values()])
        #        entropy = - sum([freq * math.log(freq) for freq in v.values()])
        #        for case, freq in sorted(v.items()):
        #            fout.write("{0}\t{1}\t{2}\t{3:.3f}\t{4:.3f}\n".format(k[0], "|".join(k[1]), case, freq, entropy))

        # Сериализуем правила
        # Структура: <Предлог>, <Список падежей> : <Правильный падеж>
        dump_data(trainfile + ".caserules.pkl", self.caserules)
        return True

    def train_cases_full(self, trainfile, threshold=1, small_diff=0.01):
        """
        Обучение снятию падежной омонимии (автоматическое извлечение правил)
        с помощью регулярных выражений.
        Полностью, начиная с предварительной обработки корпуса.

        trainfile - размеченный корпус,
        threshold - минимальная абсолютная частота вхождения правила в корпус,
        small_diff - максимальная допустимая разность между двумя вариантами правила с наибольшей вероятностью.
        """
        freqs = defaultdict(lambda: defaultdict(int))
        self.caserules = defaultdict(str)

        # Собираем частоты из корпуса
        with open(trainfile, "r", encoding="UTF8") as fin:
            sentence = []
            for line in fin:
                if line == mc.BTAG:
                    continue
                if line == mc.ETAG:
                    self.count_sentence_cases_re(sentence, freqs)
                    del sentence[:]
                    sentence = []
                    continue
                sentence.append(line.strip().split("\t"))

        # Извлекаем правила
        for k, v in freqs.items():
            good_values = {case: freq for case, freq in v.items() if freq >= threshold}
            total = sum(good_values.values())
            for case, freq in good_values.items():
                freqs[k][case] = float(freq) / total
            chosen = argmax([(case, freq) for case, freq in good_values.items()])
            if chosen is None:
                continue
            if len(chosen) != 1:
                continue
            if len(v.keys()) == 1:
                self.caserules[k] = sorted(chosen)[0]
                continue
            second = argmax([(case, freq) for case, freq in good_values.items() if case != chosen[0]])
            if second:
                if freqs[k][chosen[0]] - freqs[k][second[0]] < small_diff:
                    continue
            self.caserules[k] = sorted(chosen)[0]

        # Тестовый вывод в файл
        #with open("prep_stat_new.txt", "w", encoding="UTF8") as fout:
        #    for k, v in sorted(freqs.items()):
        #        total = sum([freq for freq in v.values()])
        #        entropy = - sum([float(freq) * math.log(float(freq) / total) / total  for freq in v.values()])
        #        entropy = - sum([freq * math.log(freq) for freq in v.values()])
        #        for case, freq in sorted(v.items()):
        #            fout.write("{0}\t{1}\t{2}\t{3:.3f}\t{4:.3f}\n".format(k[0], "|".join(k[1]), case, freq, entropy))

        # Сериализуем правила
        # Структура: <Предлог>, <Список падежей> : <Правильный падеж>
        dump_data(trainfile + ".caserules.pkl", self.caserules)
        return True

    def dump_preps(self, filename):
        """
        Запись статистики по предлогам и падежам в текстовый файл
        """
        with open(filename, "w", encoding="UTF8") as fout:
            for k, v in sorted(self.caserules.items()):
                fout.write("{0}\t{1}\t{2}\n".format(k[0], "|".join(k[1]), v))
        return True

    def load_statistics(self, trainfile, suff_len=3, process_cases=True):
        """
        Загрузка суффиксной и падежной статистики
        """
        try:
            if process_cases:
                self.caserules = unpkl_1layered_s(trainfile + ".cases.caserules.pkl")
            self.weights = unpkl_1layered_f(trainfile + "." + str(suff_len).zfill(2) + ".suffs.weights.pkl")
            self.freqs = dawg.BytesDAWG()
            self.freqs.load(trainfile + "." + str(suff_len).zfill(2) + ".suffs.freqs.dawg")
            with open(trainfile + "." + str(suff_len).zfill(2) + ".suffs.small", "rb") as fin:
                self.small = pickle.load(fin)
        except Exception as e:
            print("Tagger statistics not found!", e)
            sys.exit()

    def lemmatize(self, tokens, make_all=True):
        """
        Получение словаря нумерованных лемматизированных токенов по простому списку токенов

        make_all - подгружать все варианты нормальных форм (а не только первую),
        Если self.dater != None, для всех дат в качестве части речи указывается "DD" "MM", "YY" или "YYYY",
        а в качестве граммем - формат YYYY-MM-DD, YY-MM-DD, YYYY-MM или MM-DD с суффиксом -B, -L, -I или -U
        """
        action = self.gram_all if make_all else self.gram_first
        return dict(enumerate([tuple([token] + action(token)) for token in tokens]))

    def make_sents(self, lemmtokens):
        """
        Разбиение текста на предложения

        lemmtokens - словарь лемматизированных токенов текста вида {номер: (лемматизированный токен)}
        """
        bound = False
        sentences = []
        cur_sent = []
        for ind, info in lemmtokens.items():
            if re.match(self.splitter, info[0]): # Возможная граница предложения
                cur_sent.append((ind, info))
                if len(cur_sent) == 1:
                    bound = False
                    continue
                if not re.match(self.bad_ender, cur_sent[-2][1][0]):    # Последний токен предложения не может быть одной буквой
                    bound = True
                continue
            if bound and info[0].strip() == "": # Пробельные символы между предложениями
                cur_sent.append((ind, info))
                continue
            if bound and not re.match(self.starter, info[0]):
                bound = False
                cur_sent.append((ind, info))
                continue
            if bound and re.match(self.starter, info[0]):# and cur_sent[-1][1][0].strip() == "": # Возможное начало предложения
                sentences.append(cur_sent)
                cur_sent = []
                cur_sent.append((ind, info))
                bound = False
                continue
            cur_sent.append((ind, info))
        if cur_sent:
            sentences.append(cur_sent)
        return tuple(sentences)

    def parse_simple(self, sent_tokens, sent_words):
        """
        Снятие частеречной омонимии для однобуквенных и двухбуквенных слов предложения
        """
        short_ambigs = [ind for ind in sent_words.keys() if re.match(self.short, sent_words[ind][0])]
        for ind in short_ambigs:
            try:
                if re.match(self.splitter, sent_tokens[ind + 1][0]) and sent_words[ind][1][mc._pos] != mc.NOUN:
                    sent_words[ind][1][mc._pos] = mc.NOUN
                    sent_words[ind][1][mc._gram] = mc._abbr
            except Exception:
                continue
        return sent_words

    def parse_cases(self, sent_tokens, sent_words):
        """
        Снятие падежной омонимии слов предложения
        """
        caseambigs = [ind for ind in sent_words.keys()
                if len(sent_words[ind]) > 2
                and all(info[mc._pos] in mc._declinable for info in sent_words[ind][1:])]

        for ind in caseambigs:
            all_vars = [info for info in sent_words[ind][1:] if mc._gram in info.keys()]
            all_cases = set([x for y in [info[mc._gram].intersection(mc._cases)
                                         for info in all_vars] for x in y])
            for cur in list(range(min(sent_tokens.keys()), ind))[::-1]:
                if re.match(self.splitter, sent_tokens[cur][0]):
                    break
                if not mc._pos in sent_tokens[cur][1].keys():
                    continue
                if not sent_tokens[cur][1][mc._pos] in mc._btwn_prep_noun:
                    break
            try:
                if sent_tokens[cur][1][mc._pos] == mc.PREP:
                    prep = sent_tokens[cur][1][mc._lemma]
                else:
                    prep = mc.NOPREP
                if all_cases != {mc._nomn, mc._accs, mc._acc2} or prep != mc.NOPREP:
                    case = self.caserules[(prep, tuple(sorted(all_cases)))]
                    if case:
                        sent_words[ind] = xrestore_lemm(sent_words, case, ind)
                else:
                    sent_words[ind] = nom_case_disamb(sent_words, ind)
            except Exception:
                continue

        return True

    def parse_sent(self, sentence, radius, suff_len, small_diff, process_cases):
        """
        Снятие морфологической омонимии предложения

        sentence - предложение (список нумерованных токенов),
        radius - радиус контекста, который учитывается при выбора правильной леммы,
        suff_len - длина суффиксов, на которых основано обучение,
        small_diff - максимальная допустимая разность между двумя вариантами правила с наибольшей вероятностью,
        Если self.dater != None, для всех дат в качестве части речи указывается "DD" "MM", "YY" или "YYYY",
        а в качестве граммем - формат YYYY-MM-DD, YY-MM-DD, YYYY-MM или MM-DD с суффиксом -B, -L, -I или -U
        process_cases=True -> снимаем падежную омонимию
        """
        if len(sentence) == 1:
            return sentence
        # Словарь токенов данного предложения
        sent_tokens = dict(sentence)
        # Словарь слов данного предложения
        sent_words = {ind: info for (ind, info) in sentence if len(info[1]) > 1}
        # Список суффиксов словоформ
        suffs = [suffix((info)[0].lower(), suff_len) for (ind, info) in sorted(sent_words.items(), key=lambda x: x[0])]
        # Словарь формата {(номер_абс, номер_отн): отсортированный список суффиксов}
        suffixes = OrderedDict([((ind, rel_num), get_suffixes(lemmtoken))
                    for (rel_num, (ind, lemmtoken)) in zip(range(len(sent_words.keys())), sorted(sent_words.items(), key=lambda x: x[0]))])
        # Номера неоднозначностей (абс. и отн.)
        ambigs = [(ind, rel_num) for ((ind, rel_num), suff_list) in sorted(suffixes.items(), key=lambda x: x[0][0]) if len(suff_list) > 1]
        # Снятие частеречной омонимии для однобуквенных и двухбуквенных слов
        sent_words = self.parse_simple(sent_tokens, sent_words)

        # Снятие омонимии во всех остальных случаях

        # Набор контекстов для данного предложения
        contexts = {(num, rel_num):
            [(-i, suff) for (i, suff) in zip(range(1, radius + 1), smart_slice(suffs, rel_num - radius, rel_num)[::-1])] +
            [(i, suff) for (i, suff) in zip(range(1, radius + 1), smart_slice(suffs, rel_num + 1, rel_num + radius + 1))]
                    for (num, rel_num) in ambigs}

        # Снятие омонимии на уровне лемм
        for (ind, rel_num) in ambigs:
            suff_list = suffixes[(ind, rel_num)]
            pairs = contexts[(ind, rel_num)]
            probs = [(var, sum([get_floatDAWG(self.freqs, "{0:d}\t{1}\t{2}\t{3}".format(rel_ind, sf, " ".join(suff_list), var), self.small) * self.weights[rel_ind]
                         for (rel_ind, sf) in pairs])) for var in suff_list]
            arg_max = argmaxx(probs) # Список наиболее вероятных суффиксов

            if arg_max:
                if len(arg_max) == len(suff_list): # Если все варианты одинаковые, берем тот, который предлагает pymorphy
                    continue

                second_prob = max([prob for (var, prob) in probs if prob < arg_max[0][1]])
                if arg_max[0][1] - second_prob < small_diff: # Ограничение на разницу между двумя макс. вероятностями
                    continue

                suffitem = sorted(arg_max)[0][0].replace(mc.NOSUFF, "") # Лучший суффикс

                # Восстановление леммы по найденному суффиксу
                sent_words[ind] = restore_lemm(sent_words, suffitem, ind)

        if self.dater:  # Обработка дат, если необходимо
            self.dater.parse_dates(sent_words, sent_tokens)

        if process_cases:   # Снятие падежной омонимии, если необходимо
            self.parse_cases(sent_tokens, sent_words)

        new_sentence = []   # Предложение со снятой омонимией
        for ind, info in sentence:

            if ind in sent_words.keys():
                new_sentence.append((ind, sent_words[ind]))
            else:
                new_sentence.append((ind, info))

        return tuple(new_sentence)

    def write_stream(self, lemmtokens, fout, radius, suff_len, sent_marks, process_cases, small_diff):
        """
        Снятие морфологической омонимии текста и запись в поток вывода по предложениям

        lemmtokens - нумерованные лемматизированные токены,
        fout - поток вывода,
        radius - радиус контекста, который учитывается при выбора правильной леммы,
        suff_len - длина суффиксов, на которых основано обучение,
        sent_marks=True -> разделяем предложения
        Если self.dater != None, для всех дат в качестве части речи указывается "DD" "MM", "YY" или "YYYY",
        а в качестве граммем - формат YYYY-MM-DD, YY-MM-DD, YYYY-MM или MM-DD с суффиксом -B, -L, -I или -U,
        process_cases=True -> снимаем падежную омонимию,
        small_diff - максимальная допустимая разность между двумя вариантами правила с наибольшей вероятностью,
        """
        for sentence in self.make_sents(lemmtokens):
            self.write_sentence(self.parse_sent(sentence, radius, suff_len, small_diff, process_cases), fout, sent_marks)
        return True

    def write_sentence(self, sentence, fout, sent_marks):
        """
        Запись лемматизированного предложения в файл.

        Лемма приводится к регистру словоформы.
        Леммы женских фамилий - в женском роде (для согласования с разметкой НКРЯ).

        fout - поток вывода,
        sent_marks=True -> разделяем предложения
        """
        if sent_marks:
            fout.write(mc.BTAG)
        for (ind, info) in sentence:
            word = info[0].strip()
            if word == "":
                continue
            lemma = info[1][mc._lemma]
            grams = None

            if mc._gram in info[1].keys():
                grams = info[1][mc._gram]

                if mc._surn in grams and mc._femn in grams:
                    lemma = adjust_female(self.morph, word, lemma)

                elif mc._patr in grams:
                    lemma = adjust_patr(self.morph, word, lemma, mc._femn in grams)

            fout.write("{0}\t{1}".format(info[0], get_same_caps(word, lemma)))

            if mc._pos in info[1].keys():
                fout.write("\t" + info[1][mc._pos])
            if grams:
                fout.write("\t" + ",".join(grams))
            fout.write("\n")
        if sent_marks:
            fout.write(mc.ETAG)
        return True

    def parse_all(self, lemmtokens, outfile, radius=2, suff_len=3, sent_marks=False, process_cases=True, small_diff=0.01):
        """
        Обработка всего текста сразу (с записью результата в файл)

        lemmtokens - нумерованные лемматизированные токены,
        outfile - файл, в который будет записан обработанный текст,
        radius - радиус контекста, который учитывается при выбора правильной леммы,
        suff_len - длина суффиксов, на которых основано обучение,
        sent_marks=True -> разделяем предложения
        Если self.dater != None, для всех дат в качестве части речи указывается "DD" "MM", "YY" или "YYYY",
        а в качестве граммем - формат YYYY-MM-DD, YY-MM-DD, YYYY-MM или MM-DD с суффиксом -B, -L, -I или -U,
        process_cases=True -> снимаем падежную омонимию,
        small_diff - максимальная допустимая разность между двумя вариантами правила с наибольшей вероятностью,
        """
        with open(outfile, "w", encoding="UTF8") as fout:
            self.write_stream(lemmtokens, fout, radius, suff_len, sent_marks, process_cases, small_diff)
        return True

    def parse_chunks(self, filename, radius=2, suff_len=3, chunks=2000, sent_marks=False, process_cases=True, small_diff=0.01):
        """
        Обработка текста частями и запись результата в файл

        filename - исходный текстовый файл,
        radius - радиус контекста, который учитывается при выбора правильной леммы,
        suff_len - длина суффиксов, на которых основано обучение,
        chunks - примерная длина одного чанка (в строках),
        sent_marks=True -> разделяем предложения
        Если self.dater != None, для всех дат в качестве части речи указывается "DD" "MM", "YY" или "YYYY",
        а в качестве граммем - формат YYYY-MM-DD, YY-MM-DD, YYYY-MM или MM-DD с суффиксом -B, -L, -I или -U,
        process_cases=True -> снимаем падежную омонимию,
        small_diff - максимальная допустимая разность между двумя вариантами правила с наибольшей вероятностью,
        """
        buff = []
        counter = 0
        tokens = {}
        tok_r = Tokenizer()
        # Читаем тестовый файл
        with open(filename, "r", encoding = "UTF8") as fin, open(filename + ".lemma", "w", encoding = "UTF8") as fout:
            for line in fin:
                if len(buff) >= chunks and re.search(self.splitter, buff[-1]):
                    part_1 = re.split(self.splitter, buff[-1])[0] + re.findall(self.splitter, buff[-1])[0]
                    part_rest = buff[-1][len(part_1) + 1:]
                    self.parse_chunk(buff[:-1] + [part_1], fout, tok_r, radius, suff_len, sent_marks, process_cases, small_diff)
                    del buff[:]
                    buff = [part_rest]
                    counter += 1
                    print("chunk", counter, "done!")
                buff.append(line)
            if buff != []:
                self.parse_chunk(buff, fout, tok_r, radius, suff_len, sent_marks, process_cases, small_diff)

    def parse_chunk(self, buff, fout, tok_r, radius, suff_len, sent_marks, process_cases, small_diff):
        """
        Снятие морфологической омонимии текстового фрагмента и запись результата в открытый поток вывода

        buff - текущий текстовый фрагмент для обработки,
        fout - поток вывода,
        tok_r - используемый токенизатор,
        radius - радиус контекста, который учитывается при выбора правильной леммы,
        suff_len - длина суффиксов, на которых основано обучение,
        sent_marks=True -> разделяем предложения
        Если self.dater != None, для всех дат в качестве части речи указывается "DD" "MM", "YY" или "YYYY",
        а в качестве граммем - формат YYYY-MM-DD, YY-MM-DD, YYYY-MM или MM-DD с суффиксом -B, -L, -I или -U,
        process_cases=True -> снимаем падежную омонимию,
        small_diff - максимальная допустимая разность между двумя вариантами правила с наибольшей вероятностью,
        """
        lemmtokens = self.lemmatize(tok_r.tokenize("".join(buff))) # Словарь токенов
        self.write_stream(lemmtokens, fout, radius, suff_len, sent_marks, process_cases, small_diff)
        return True

    def get_parsed_sents(self, tokens, radius=2, suff_len=3, process_cases=True, small_diff=0.01):
        """
        Получение списка предложений со снятой морфологической омонимией

        tokens - список токенов исходного текста,
        radius - радиус контекста, который учитывается при выбора правильной леммы,
        suff_len - длина суффиксов, на которых основано обучение,
        Если self.dater != None, для всех дат в качестве части речи указывается "DD" "MM", "YY" или "YYYY",
        а в качестве граммем - формат YYYY-MM-DD, YY-MM-DD, YYYY-MM или MM-DD с суффиксом -B, -L, -I или -U,
        process_cases=True -> снимаем падежную омонимию,
        small_diff - максимальная допустимая разность между двумя вариантами правила с наибольшей вероятностью,
        """
        return [self.parse_sent(sentence, radius, suff_len, small_diff, process_cases) for sentence in self.make_sents(self.lemmatize(tokens))]

if __name__ == "__main__":

    filename = os.path.join(os.path.dirname(sys.argv[0]), sys.argv[1])
    trainfile = os.path.join(os.path.dirname(sys.argv[0]),"dicts/ruscorpora.txt.lemma")
    prepsfile = os.path.join(os.path.dirname(sys.argv[0]),"preps_stat.txt")

    print("STARTED:", str(datetime.now()))
    start = time.time()

    morph = pymorphy2.MorphAnalyzer()  # Подгружаем русский словарь

    tok = Tokenizer()   # Подгружаем токенизатор
    dater = Dater() # Подгружаем обработчик дат
    tagger = Tagger(morph, dater)  # Подгружаем тэггер

    t = time.time()
    #tagger.prepare_cases(trainfile)
    #print("Cases prepared! It took", time.time() - t)
    #t = time.time()
    #tagger.train_cases(trainfile + ".cases") # Обучаем тэггер падежам
    #print("Cases trained! It took", time.time() - t)

    #tagger.prepare_corpus(trainfile, 3)
    #tagger.prepare_corpus(trainfile, 4)
    #tagger.prepare_corpus(trainfile, 5)
    #print("Corpus prepared!")

    #tagger.train(trainfile + ".03.suffs", 3) # Обучаем тэггер суффиксам
    #print("Suffix model trained!")
    tagger.load_statistics(trainfile, 3)   # Загружаем суффиксную статистику

    #tagger.dump_preps(prepsfile)   # Выписываем правила падежей в зависимости от предлогов в текстовый файл

    print("Statistics loaded! It took", time.time() - start, "\nParsing file...")

    tokens = []
    with open(filename, "r", encoding="UTF8") as fin:    # Читаем тестовый файл
        tokens = tok.tokenize(fin.read()) # Список токенов
    # Записываем результат в файл
    tagger.parse_all(tagger.lemmatize(tokens), filename + ".lemma", sent_marks=True)

    print("FINISHED:", str(datetime.now()))
    print("Time elapsed: ", time.time() - start)
