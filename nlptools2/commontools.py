#!/usr/bin/python
# -*- coding: utf-8 -*-

# Common tools for iterables (Python 3)

import struct
from collections import deque

def eq(first, second, eps=0.00000000000000000001):
    """
    Проверка равенства чисел типа float
    """
    return abs(first - second) <=  eps

def argmax(lst):
    """
    Нахождение аргмаксимума (возвращает только аргмаксимум)
    """
    if not lst:
        return None
    return [x for (x, y) in lst if eq(y, max([second for (first, second) in lst]))]

def argmaxx(lst):
    """
    Нахождение аргмаксимума (возвращает кортеж (аргмаксимум, максимум))
    """
    if not lst:
        return None
    return [(x, y) for (x, y) in lst if eq(y, max([second for (first, second) in lst]))]

def MI(f_xy, f_x, f_y, N):
    """
    Взаимная информация
    """
    return float(f_xy * N) / (f_x * f_y)

def get_DAWG(freqs, word):
    """
    Извлечение слова из DAWG
    """
    if word in freqs:
        return freqs[word]
    return 0.0

def get_floatDAWG(freqs, word, small):
    """
    Извлечение слова из BytesDAWG
    """    
    if word in freqs:
        return struct.unpack("f", freqs[word][0])[0]
    return small

def smart_range(nums, num, radius):
    """
    Обертка среза range
    """
    start = num - radius
    if start < 0:
        start = 0
    end = num + radius
    if end >= len(nums):
        return list(range(start, num)) + list(range(num + 1, len(nums)))
    return list(range(start, num)) + list(range(num + 1, num + radius + 1))

def smart_slice(items, start, end):
    """
    Обертка среза списка
    """
    if start < 0:
        start = 0
    if end < len(items):
        return items[start:end]
    else:
        return items[start:]
 
def read_file(filename):
    """
    Чтение файла с текстом на русском языке в неизвестной кодировке (cp1251 или UTF-8)
    """
    try:
        # Определение кодировки: ASCII
        with open(filename, "r") as tfile:
            text = tfile.read().decode("cp1251").encode("UTF8").decode("UTF8")  
    except Exception:
        # Определение кодировки: UTF-8
        with open(filename, "r", encoding="UTF8") as tfile:
            text = tfile.read()
    return text

