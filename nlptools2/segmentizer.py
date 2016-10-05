#!/usr/bin/python
# -*- coding: utf-8 -*-

# A simple segmentizer for Python 3 (splits first list of tokens into sentences)

import sys
import os
import string
import re

from tokenizer import Tokenizer

class Segmentizer(object):
    """
    A simple segmentizer for Python 3
    """

    def __init__(self):
        """
        Initialize
        """
        self.splitter = re.compile("[.?!]+")
        self.starter = re.compile("[А-ЯЁA-Z\d\"\'\(\)\[\]~`«s-]")
        self.bad_ender = re.compile("^[А-ЯЁа-яёA-Za-z][а-яёa-z]?$")

    def segmentize(self, tokens):
        """
        Split text into sentences.
        """
        bound = False
        sentences = []
        cur_sent = []
        toks = dict(enumerate(tokens))
        
        for ind, info in toks.items():
            if re.match(self.splitter, info): # Возможная граница предложения
                cur_sent.append((ind, info))
                if len(cur_sent) == 1:
                    bound = False
                    continue
                if not re.match(self.bad_ender, cur_sent[-2][1]):    # Последний токен предложения не может быть одной буквой
                    bound = True
                continue
            if bound and info.strip() == "": # Пробельные символы между предложениями
                cur_sent.append((ind, info))
                continue
            if bound and not re.match(self.starter, info):
                bound = False
                cur_sent.append((ind, info))
                continue
            if bound and re.match(self.starter, info): # Возможное начало предложения
                sentences.append(cur_sent)
                cur_sent = []
                cur_sent.append((ind, info))
                bound = False
                continue
            cur_sent.append((ind, info))
        if cur_sent:
            sentences.append(cur_sent)
        return tuple(sentences)

if __name__ == "__main__":

    text = "Токенизация (tokenization, lexical analysis, графематический анализ, лексический анализ) - это когда в тексте выделяются слова, числа, и другие токены. Например, нахождение границ предложений. "  

    tok_r = Tokenizer()
    tokens = tok_r.tokenize(text)
    for item in tokens:
        print("TOKEN:", item)    

    mySegmentizer = Segmentizer()          
    sentences = mySegmentizer.segmentize(tokens)
    for sentence in sentences:
        print("SENTENCE:", " ".join(x[1].strip() for x in sentence))
        
