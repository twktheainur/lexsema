#!/usr/bin/python
# -*- encoding: utf-8 -*-

"""
Обработка дат для лемматизации на основе правил и регулярных выражений (Python 3)
"""

import re

import mc

YYYY = "YYYY"
MM = "MM"
DD = "DD"
YYYY_MM_DD = "YYYY-MM-DD"
YY_MM_DD = "YY-MM-DD"
MM_DD = "MM-DD"
YYYY_MM = "YYYY-MM"
_B = "-B"
_I = "-I"
_U = "-U"
_L = "-L"

class Dater(object):
    """
    Обработчик дат (для русского языка).
    Если слово является датой или ее частью, в качестве части речи указывается "DD" "MM", "YY" или "YYYY",
    а в качестве граммем - формат (YYYY-MM-DD, YY-MM-DD, YYYY-MM или MM-DD) с суффиксом -B, -L, -I или -U.
    
    Даты в тексте размечаются в формате BILOU (begin-in-last-out-unit).
    Например:
    04    04    DD    YYYY-MM-DD-B
    июня    06    MM    YYYY-MM-DD-I
    2013    2013    YYYY    YYYY-MM-DD-L
    """

    def __init__(self):
        """
        Инициализация.
        """

        # Рег. выражения для лемматизации
        self.day = re.compile("^\d{1,2}(?:-?[оегмуы]{2,3})?$")
        self.dig = re.compile("^\d+")
        self.date = re.compile("^\d+(?:[.:/-]\d+)+$")
        self.datesplitter = re.compile("[.:/-]")
        self.year = re.compile("^\d{4}г?$")
        # Длина месяца (в днях)
        self.months = {1: 31, 2: 29, 3: 31, 4: 30, 5: 31, 6: 30, 7: 31, 8: 31, 9: 30, 10: 31, 11: 30, 12: 31}
        # Названия месяцев (с учетом сокращений и возможных ошибок лемматизации)
        self.monthnames = {"январь": 1, "янв": 1, "февраль": 2, "фев": 2,
                           "март": 3, "марта": 3, "апрель": 4, "апр": 4,
                           "май": 5, "мая": 5, "июнь": 6, "июль": 7,
                           "август": 8, "авг": 8, "сентябрь": 9, "сен": 9,
                           "сент": 9, "октябрь": 10, "окт": 10, "ноябрь": 11,
                           "ноября": 11, "декабрь": 12, "дек": 12}
        # Максимально допустимый год
        self.maxyear = 2050

    def is_date(self, word):
        """
        Проверка слова на соответствие рег. выражению даты.
        """
        return re.match(self.date, word)

    def correct_date(self, month, day, year=None):
        """
        Проверка того, что данная тройка (месяц, день, год) может являться датой (год может не указываться).
        Возвращает дату в формате ISO или None.
        """
        if year:
            if year >= self.maxyear or year <= 0:
                return None
        try:
            if day > self.months[month] or day <= 0:
                return None
            date = "{0:0>2d}-{1:0>2d}".format(month, day)
            if year:
                date = str(year) + "-" + date
            return date
        except Exception:
            return None

    def check_double_date(self, parts):
        """
        Приведение даты к формату MM-DD или YYYY-MM.
        Возвращает дату в формате ISO или None.
        """
        (p1, p2) = parts       
        if len(p1) <= 2 and len(p2) <= 2:  
            date1 = self.correct_date(int(p2), int(p1)) 
            if date1:
                return (date1, MM_DD)          
            date2 = self.correct_date(int(p1), int(p2))
            if date2:
                return (date2, MM_DD)
            return None
        if len(p1) == 4 and int(p1) < self.maxyear and len(p2) <= 2 and int(p2) <= 12:
            return (".".join((p2.zfill(2), p1)), YYYY_MM)
        if len(p2) == 4 and int(p2) < self.maxyear and len(p1) <= 2 and int(p1) <= 12:
            return (".".join((p1.zfill(2), p2)), YYYY_MM)
        return None

    def check_triple_date(self, parts):
        """
        Приведение даты к формату YY_MM-DD или YYYY-MM-DD.
        Возвращает дату в формате ISO или None.
        """
        (p1, p2, p3) = parts
        if len(p1) <= 2 and len(p2) <= 2 and (len(p3) == 2 or len(p3) == 4) and 0 < int(p3) < self.maxyear:           
            date1 = self.correct_date(int(p2), int(p1), int(p3))
            dtype = YYYY_MM_DD if len(p3) == 4 else YY_MM_DD
            if date1:      
                return (date1, dtype)
            date2 = self.correct_date(int(p1), int(p2), int(p3))    
            if date2:
                return (date2, dtype)
            return None
        if (len(p1) == 2 or len(p1) == 4) and len(p2) <= 2 and len(p3) <= 2 and 0 < int(p1) < self.maxyear:            
            date1 = self.correct_date(int(p2), int(p3), int(p1))
            dtype = YYYY_MM_DD if len(p1) == 4 else YY_MM_DD
            if date1:    
                return (date1, dtype) 
            date2 = self.correct_date(int(p3), int(p2), int(p1))
            if date2:
                return (date2, dtype)
        return None

    def check_date(self, word):
        """
        Проверка даты и приведение к формату ISO (YYYY-MM-DD).      
        word - предполагаемая дата в произвольном формате (числа, разделенные точкой/двоеточием/слешем).
        Возвращает дату в формате ISO или None.
        """
        parts = re.split(self.datesplitter, word)
        if len(parts) < 2 or len(parts) > 3:
            return None
        if any(not len(x) in {1, 2, 4} for x in parts):
            return None      
        if len(parts) == 2: # Количество частей = 2
            return self.check_double_date(parts)
        # Количество частей = 3
        return self.check_triple_date(self, parts)

    def parse_days(self, inds, sent_words, sent_tokens):
        """
        Обработка дней.
        """
        digits = [ind for ind, info in sent_tokens.items() if re.match(self.day, info[0])]
        for ind in digits:
            word = sent_words[ind][0]
            try:
                day = int(word)
            except Exception:
                try:
                    day = int(re.findall(self.dig, word)[0])
                except Exception:
                    print("Date parse error occurred at:", word)
                    sys.exit()
            try:
                next_ind = inds[inds.index(ind) + 1]
                next_lexeme = sent_words[next_ind][1][mc._lemma]
                month_id = self.monthnames[next_lexeme]
                
                if day <= self.months[month_id]:
                    sent_words[ind] = (sent_words[ind][0], {mc._lemma: sent_words[ind][0].zfill(2), mc._pos: DD, mc._gram: {MM_DD + _B}}) 
                    sent_words[next_ind] = (sent_words[next_ind][0], {mc._lemma: "{0:0>2d}".format(month_id), mc._pos: MM, mc._gram: {MM_DD + _L}})
                    next_ind2 = inds[inds.index(ind) + 2]
                if not re.match(self.year, sent_words[next_ind2][1][mc._lemma]):
                    continue             
                if mc._gram in sent_words[next_ind2][1].keys():
                    if sent_words[next_ind2][1][mc._gram] != YYYY + _U:
                        continue
                elif int(sent_words[next_ind2][0]) >= self.maxyear:
                    continue
                sent_words[ind] = (sent_words[ind][0],
                                   {mc._lemma: sent_words[ind][0].zfill(2), mc._pos: DD, mc._gram: {YYYY_MM_DD + _B}})
                sent_words[next_ind] = (sent_words[next_ind][0],
                                        {mc._lemma: "{0:0>2d}".format(month_id), mc._pos: MM, mc._gram: {YYYY_MM_DD + _I}})
                sent_words[next_ind2] = (sent_words[next_ind2][0],
                                         {mc._lemma: sent_words[next_ind2][1][mc._lemma], mc._pos: YYYY, mc._gram: {YYYY-MM-DD + _L}})
            except Exception:
                continue
        return True

    def parse_months(self, inds, sent_words, sent_tokens):
        """
        Обработка месяцев.       
        """
        months = [ind for ind, info in sent_words.items() if info[1][mc._lemma] in set(self.monthnames.keys())]
        for ind in months:
            try:
                next_ind = inds[inds.index(ind) + 1]
                if not re.match(self.year, sent_words[next_ind][1][mc._lemma]):
                    continue
                if mc._gram in sent_words[next_ind][1].keys():
                    if sent_words[next_ind][1][mc._gram] != YYYY + _U:
                        continue
                elif int(sent_words[next_ind][0]) >= self.maxyear:
                    continue
                month_id = self.monthnames[sent_words[ind][1][mc._lemma]]
                sent_words[ind] = (sent_words[ind][0],
                                   {mc._lemma: "{0:0>2d}".format(month_id), mc._pos: MM, mc._gram: {YYYY_MM + _B}})
                sent_words[next_ind] = (sent_words[next_ind][0],
                                        {mc._lemma: sent_words[next_ind][1][mc._lemma], mc._pos: YYYY, mc._gram: {YYYY_MM + _L}})
            except Exception:
                continue
        return True

    def parse_years(self, inds, sent_words, sent_tokens):
        """
        Обработка лет.
        """
        years = [ind for ind, info in sent_tokens.items() if re.match(self.year, info[1][mc._lemma])]  
        for ind in years:
            year_str = sent_words[ind][0]
            try:
                year = int(year_str)
            except Exception:
                year = int(year_str[:4])
                
            if year >= self.maxyear:
                continue
            
            if len(year_str) == 5:  # Запись вида 2011г
                sent_words[ind] = (year_str, {mc._lemma: year_str[:-1], mc._pos: YYYY, mc._gram: {YYYY + _U}})
                continue         
            try:    # Запись вида 2011 - проверяем, что следующя лемма - ГОД или Г 
                next_ind = inds[inds.index(ind) + 1]
                if sent_words[next_ind][1][mc._lemma] in {"г", "год"}:      
                    sent_words[ind] = (year_str, {mc._lemma: year_str, mc._pos: YYYY, mc._gram: {YYYY + _U}})
                    if sent_words[next_ind][1][mc._pos] != mc.NOUN:
                        sent_words[next_ind] = (sent_words[next_ind][0], {mc._lemma: "год", mc._pos: mc.NOUN, mc._gram: {mc._masc}})
            except Exception:
                continue
        return True
    
    def parse_dates(self, sent_words, sent_tokens):
        """
        Обработка дат в предложении.
        
        sent_words - словарь (нумерованный список) лемматизированных слов предложения, sent_tokens - токенов
        Каждое слово имеет формат [словоформа, {парадигма_1}, {парадигма_2}, ...]
        Парадигмы имеют вид словаря {mc._lemma: norm, mc._pos: class, mc._gram: info}
        """
        inds = sorted(sent_words.keys())
        self.parse_years(inds, sent_words, sent_tokens) # Обработка лет
        self.parse_days(inds, sent_words, sent_tokens)  # Обработка дней
        self.parse_months(inds, sent_words, sent_tokens)  # Обработка месяцев
        return True

