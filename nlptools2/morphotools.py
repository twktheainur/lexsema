#!/usr/bin/python
# -*- coding: utf-8 -*-

# Morphologic tools for Russian (Python 3) using pymorphy2

import itertools
import re

import mc
from commontools import *


def extract_words(sentences):
    """
    Выборка всех слов из наборов токенов
    """
    words = {ind: info for sentence in sentences for (ind, info) in sentence if len(info[1]) > 1}

def is_synable():
    """
    Проверка вхождения во множество синонимизируемых частей речи
    """
    return {mc.NOUN, mc.VERB, mc.INFN, mc.ADJF, mc.ADVB, mc.ADJS, mc.PRTF, mc.PRTS}

def good_pos():
    """
    Множество полезных для синонимизации частей речи
    """
    return {mc.NOUN, mc.VERB, mc.INFN, mc.ADJF, mc.ADVB, mc.ADJS, mc.PRTF, mc.PRTS, mc.PREP}

def non_nominal(all_vars):
    """
    Выбор варианта нормальной формы с граммемами без именительного падежа
    """
    return [info for info in all_vars if not mc._nomn in info[mc._gram]]

def suffix(word, i):
    """
    Суффикс длины i
    """
    if i == 0:
        return mc.NOSUFF
    if len(word) > i:
        return word[-i:]
    return word

def cut_suffix(word, suff):
    """
    Отрезание от слова суффикса, если он есть
    """
    if word.endswith(suff):
        return word[:word.find(suff)]
    return word

def get_suffixes(lemmtoken):
    """
    Извлечение набора суффиксов для списка лемм
    """
    word = lemmtoken[0].lower()
    lemms = [x[mc._lemma].lower() for x in lemmtoken[1:]]
    stem = longest_common([word] + lemms) # longest common prefix
    return sorted(list(set([suffix(lemma, len(lemma) - len(stem)) for lemma in lemms])))

def longest_common(words):
    """
    Наибольший общий префикс нескольких слов
    """
    char_tuples = zip(*words)
    prefix_tuples = itertools.takewhile(lambda x: all(x[0] == y for y in x), char_tuples)
    return "".join(x[0] for x in prefix_tuples)

def smart_dict_slice(sentence, num, radius):
    """
    Обертка среза словаря (левый и правый контексты слова)
    """
    ind_list = [ind for (ind, info) in sentence if mc._pos in info[1].keys()]
    num_index = ind_list.index(num)
    sliced = set(smart_slice(ind_list, num_index - radius, num_index) + smart_slice(ind_list, num_index + 1, num_index + radius))
    context = set([word_info[1][mc._lemma] for (ind, word_info) in sentence if ind in sliced])        
    return context

def ngram_slice(sentence, num, N):
    """
    Обертка среза словаря (левый контекст слова)
    """
    inds = [ind for (ind, info) in sentence if mc._pos in info[1].keys()] # Индексы всех слов в предложении
    num_ind = inds.index(num)
    ngram = smart_slice(inds, num_ind - N + 1, num_ind + 1)
    return [info[1][mc._lemma] for (ind, info) in sentence if ind in ngram]

def restore_lemm(sent_words, suffitem, word_ind):
    """
    Восстановление леммы по суффиксу
    """
    word = sent_words[word_ind][0]
    lemms = [x[mc._lemma].lower() for x in sent_words[word_ind][1:]]      
    best_lemma = longest_common([word.lower()] + lemms) + suffitem # Лучшая лемма
    return tuple([word] + [info for info in sent_words[word_ind][1:] if info[mc._lemma].lower() == best_lemma])

def good_lemm(norm, case):
    """
    Проверяем, подходит ли данный вариант нормальной формы при восстановлении по падежу
    """
    if not mc._gram in norm.keys():
        return True
    if case in re.split(r"[,\s]", norm[mc._gram]):
        return True
    return False

def xrestore_lemm(sent_words, case, ind):
    """
    Восстановление леммы по падежу
    """
    good_lemms = [info for info in sent_words[ind][1:] if good_lemm(info, case)]
    if good_lemms:
        return tuple([sent_words[ind][0]] + good_lemms)
    return sent_words[ind]

def concord_verb(verb_grams, noun_grams):
    """
    Проверка согласования существительного с глаголом
    """
    diff = noun_grams.difference(verb_grams)
    same_amount = not diff.intersection({mc._sing, mc._plur})
    if same_amount:
        if mc._plur in noun_grams:
            return True
        elif mc._past in verb_grams:
            return not diff.intersection(mc._genders)
        else:
            return True
    return False

def nom_case_disamb(sent_words, word_ind):
    """
    Снятие омонимии между именительным и винительным падежами
    """

    inds = list(sent_words.keys())
    targets = [info for info in sent_words[word_ind][1:] if mc._lemma in info.keys()]
   
    if word_ind == inds[0]: # Если это первое слово в предложении
        return sent_words[word_ind]    
    
    prev_verbs = {ind: info for (ind, info) in sent_words.items()
                  if ind < word_ind and info[1][mc._pos] in {mc.VERB, mc.INFN}} # Все предшествующие глаголы в предложении

    if prev_verbs == {}:
        return sent_words[word_ind]
    
    min_verb_ind = min(prev_verbs.keys())
    max_verb_ind = max(prev_verbs.keys())
    last_verb = [x for ind, x in prev_verbs.items() if ind == max_verb_ind][0]

    # Если существительное, предположительно, является дополнением, оно не может быть в именительном падеже

    min_noun_inds = [ind for ind in inds if ind < max_verb_ind and sent_words[ind][1][mc._pos] in {mc.NOUN, mc.NPRO}]
    nom_noun_inds = [ind for ind in min_noun_inds if _nomn in sent_words[ind][1][mc._gram]]

    if not nom_noun_inds:   # Впереди нет существительных в именительном падеже
        # В одном из вариантов существительное согласовано с глаголом слева?
        good_targets = any(target for target in targets if concord_verb(last_verb[1][mc._gram], target[mc._gram]))
        if good_targets:    # Да
            return tuple([sent_words[word_ind][0]] + good_targets)
        reslist = non_nominal(targets)  # Нет - значит, падеж винительный
        if reslist:
            return tuple([sent_words[word_ind][0]] + reslist)

    if nom_noun_inds:   # Впереди есть существительные в именительном падеже
        if any(concord_verb(last_verb[1][mc._gram], sent_words[ind][1][mc._gram]) for ind in nom_noun_inds):
            reslist = non_nominal(targets)  # Где-то впереди было подлежащее - значит, падеж винительный
            if reslist:
                return tuple([sent_words[word_ind][0]] + reslist)
    return sent_words[word_ind]

def get_same_caps(pattern, word):
    """
    Соответствие прописных и строчных букв (возвращает слово такого же типа, что и заданное)
    """
    if pattern.istitle():
        items = [part.lower() for part in word.strip().split()]
        if len(items) == 1:
            return word.title()
        items[0] = items[0].title()
        return " ".join(items)
    if pattern.islower():
        return word.lower()
    if pattern.isupper():
        return word.upper()
    return word.lower()

def concord(var_name, *var_adjs):
    """
    Проверка согласования существительного и одного или нескольких прилагательных в роде/числе/падеже
    """
    grams_name = var_name[mc._gram]
    good = []
    for var_adj in var_adjs:
        grams_adj = var_adj[mc._gram]
        diff = grams_adj.difference(grams_name)
        if diff == set() or diff == {_plur}:
            good.append(var_adj)
        else:
            break
    return good

def adjust_female(morph, word, true_lemma):
    true_paradigm = [x for x in morph.parse(word) if x.normal_form == true_lemma]
    if not true_paradigm:
        return true_lemma
    return true_paradigm[0].inflect({mc._sing, mc._nomn, mc._femn}).word

def adjust_patr(morph, word, true_lemma, femn=False):
    true_paradigm = [x for x in morph.parse(word) if x.normal_form == true_lemma]
    if not true_paradigm:
        return true_lemma
    if femn:
        return true_paradigm[0].inflect({mc._sing, mc._nomn, mc._femn, mc._patr}).word 
    return true_paradigm[0].inflect({mc._sing, mc._nomn, mc._masc, mc._patr}).word    

def nearest_left_noun(sentence, words, ind):
    """
    Нахождение ближайшего существительного слева, согласованного с данным
    """
    inds = list(words.keys())
    sent = dict(sentence)
    if ind == inds[0]:
        return None
    prev_ind = inds[inds.index(ind) - 1]
    if words[prev_ind][0][0].islower():
        return None
    if any(sent[i][0].strip() != "" for i in range(prev_ind + 1, ind)):
        return None
    nouns = [var for var in words[prev_ind][1:] if var[mc._pos] == mc.NOUN]  # Варианты соседа слева
    if not nouns:
        return None
    names = [var for var in words[ind][1:] if mc._name in var[mc._gram]] # Варианты данного слова
    combinations = [(var_adj, var_name) for var_adj, var_name in itertools.product(nouns, names) if concord(var_name, var_adj)]
    return (prev_ind, combinations)

def nearest_right_noun(sentence, words, ind):
    """
    Нахождение ближайших двух существительных справа, согласованных с данным
    """
    inds = list(words.keys())
    sent = dict(sentence)
    if ind == inds[-1]:
        return None
    next_ind = inds[inds.index(ind) + 1]
    if words[next_ind][0][0].islower():
        return None
    if any(sent[i][0].strip() != "" for i in range(ind + 1, next_ind)):
        return None
    nouns = [var for var in words[next_ind][1:] if var[mc._pos] == mc.NOUN] # Варианты соседа справа
    if not nouns:
        return None
    names = [var for var in words[ind][1:] if _name in var[mc._gram]] # Варианты данного слова
    combinations = [(var_name, var_adj)
                    for var_name, var_adj
                    in itertools.product(names, nouns)
                    if concord(var_name, var_adj)]
    if inds[-1] == next_ind:
        return (next_ind, combinations)

    next_ind2 = inds[inds.index(ind) + 2]
    if words[next_ind2][0][0].islower():
        return (next_ind, combinations)
    if any(sent[i][0].strip() != "" for i in range(next_ind + 1, next_ind2)):
        return (next_ind, combinations)
    nouns2 = [var for var in words[next_ind2][1:] if var[mc._pos] == mc.NOUN]
    combinations = [(var_name, concord(var_name, var_adj, var_adj2))
                    for var_name, var_adj, var_adj2 in
                    itertools.product(nouns2, sent[next_ind][1:], sent[next_ind2][1:])
                    if concord(var_name, var_adj, var_adj2)]
    return (next_ind, next_ind2, combinations)

