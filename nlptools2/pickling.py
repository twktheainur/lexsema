#!/usr/bin/python
# -*- coding: utf-8 -*-

from collections import defaultdict
import pickle

# Функции-обертки для загрузки/выгрузки многоуровневых словарей с помощью модуля cPickle (Python 2.7)

def inner_lambda_float():
    """
    Обертка для lambda (одноуровневая, float)
    """
    return defaultdict(float)

def inner_lambda_int():
    """
    Обертка для lambda (одноуровневая, int)
    """
    return defaultdict(int)

def inner_lambda_str():
    """
    Обертка для lambda (одноуровневая, str)
    """
    return defaultdict(str)

def inner_lambda_set():
    """
    Обертка для lambda (одноуровневая, set)
    """
    return defaultdict(set)

def inner_lambda_tuple():
    """
    Обертка для lambda (одноуровневая, tuple)
    """
    return defaultdict(tuple)

def inner_func_float():
    """
    Обертка для lambda (двухуровневая, float)
    """
    return defaultdict(inner_lambda_float)

def inner_func_tuple():
    """
    Обертка для lambda (двухуровневая, tuple)
    """
    return defaultdict(inner_lambda_tuple)

def inner_func_float2():
    """
    Обертка для lambda (треххуровневая, float)
    """
    return defaultdict(inner_func_float)

def inner_func_int():
    """
    Обертка для lambda (двухуровневая, int)
    """
    return defaultdict(inner_lambda_int)

def inner_func_str():
    """
    Обертка для lambda (двухуровневая, str)
    """
    return defaultdict(inner_lambda_str)

def inner_func_set():
    """
    Обертка для lambda (двухуровневая, set)
    """
    return defaultdict(inner_lambda_set)

def unpkl_1layered_f(picklefreqs):
    """
    Подгружает одноуровневый словарь типа float
    """
    freqs = defaultdict(float)
    with open(picklefreqs, "rb") as fin:
        freqs = pickle.load(fin)
    return freqs
 
def unpkl_1layered_i(picklefreqs):
    """
    Подгружает одноуровневый словарь типа int
    """
    freqs = defaultdict(int)
    with open(picklefreqs, "rb") as fin:
        freqs = pickle.load(fin)
    return freqs

def unpkl_1layered_s(picklefreqs):
    """
    Подгружает одноуровневый словарь типа str
    """
    freqs = defaultdict(str)
    with open(picklefreqs, "rb") as fin:
        freqs = pickle.load(fin)
    return freqs

def unpkl_1layered_sets(picklefreqs):
    """
    Подгружает одноуровневый словарь типа set
    """
    sets = defaultdict(set)
    with open(picklefreqs, "rb") as fin:
        sets = pickle.load(fin)
    return sets

def unpkl_2layered_f(picklefile):
    """
    Подгружает двухуровневый словарь типа float
    """
    freqs = defaultdict(inner_lambda_float)
    with open(picklefile, "rb") as fin:
        freqs = pickle.load(fin)
    return freqs

def unpkl_2layered_i(picklefile):
    """
    Подгружает двухуровневый словарь типа int
    """
    freqs = defaultdict(inner_lambda_int)
    with open(picklefile, "rb") as fin:
        freqs = pickle.load(fin)
    return freqs

def unpkl_2layered_s(picklefile):
    """
    Подгружает двухуровневый словарь типа str
    """
    words = defaultdict(inner_lambda_str)
    with open(picklefile, "rb") as fin:
        words = pickle.load(fin)
    return words

def unpkl_2layered_set(picklefile):
    """
    Подгружает двухуровневый словарь типа set
    """
    words = defaultdict(inner_lambda_set)
    with open(picklefile, "rb") as fin:
        words = pickle.load(fin)
    return words

def unpkl_2layered_tuple(picklefile):
    """
    Подгружает двухуровневый словарь типа tuple
    """
    words = defaultdict(inner_lambda_tuple)
    with open(picklefile, "rb") as fin:
        words = pickle.load(fin)
    return words

def unpkl_3layered_i(picklefile):
    """
    Подгружает трехуровневый словарь типа int
    """
    freqs = defaultdict(inner_func_int)
    with open(picklefile, "rb") as fin:
        freqs = pickle.load(fin)
    return freqs

def unpkl_3layered_f(picklefile):
    """
    Подгружает трехуровневый словарь типа float
    """
    freqs = defaultdict(inner_func_float)
    with open(picklefile, "rb") as fin:
        freqs = pickle.load(fin)
    return freqs

def unpkl_set(picklefile):
    """
    Подгружает множество
    """
    wordset = set()
    with open(picklefile, "rb") as fin:
        wordset = pickle.load(fin)
    return wordset

def dump_data(filename, data):
    """
    Сериализация
    """
    with open(filename, "wb") as fout:
        pickle.dump(data, fout, pickle.HIGHEST_PROTOCOL)
    return True
