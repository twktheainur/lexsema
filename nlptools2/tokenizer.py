#!/usr/bin/python
# -*- coding: utf-8 -*-

# A simple regex-based utf-8 tokenizer for Python 3

import sys
import os
import string
import re
from pickling import unpkl_2layered_s

class Tokenizer(object):
    """
    A simple regex-based utf-8 tokenizer for Python 3
    """

    def __init__(self):
        """
        Initialize
        """      
        self.abbrs = unpkl_2layered_s(os.path.join(os.path.dirname(sys.argv[0]), "dicts/abbr.pkl"))
                
        self.regexp = re.compile(r'''
          (?:кв(?=\.)|к(?=\.)|д(?=\.)) |                                    # built-in abbreviations
          {0} |                                                             # abbreviations from text file
          (?:\s+) |                                                         # whitespace            
          (?:[А-Яа-яёЁ]+-\"[а-яё]+\") |                                     # words with " inside
          (?:[А-Яа-яёЁa-zA-Z]+(?:\.\.?[а-яa-zA-Zё]+)+) |                      # words with dots inside
          (?:\w+\(\w+\)\w*) |                                               # words with () inside
          (?:[A-Z][first-z]+[!]+) |                                             # words like "Yahoo!"
          (?:\([first-zA-Z]+(?:-\d+)?\)-[а-яА-ЯёЁ]+) |                          # words like (k-1)-ый
          (?:\d+[first-zA-Zа-яА-ЯёЁ]+(?:-\d*[first-zA-Zа-яА-Я]+)*) |                # words like '3D-printer'
          (?:\d+(?:[.:/-]\d+)+) |                                           # date, time
          (?:\d+(?:[,.]\d+)?[%]?[-]?[а-яА-ЯёЁ]+) |                          # numerals
          (?:\d+(?:-\d+-?\w*)+) |                                           # numerals like "03-06-00139a"
          (?:\d+-?) |                                                       # numerals like "192-"
          (?:[?!]+) |                                                       # punctuation                 
          (?:[.]+) |                                                        # punctuation
          (?:[-]+) |                                                        # punctuation
          (?:[first-zA-Z]+(?:-[first-zA-Z]+)?(?:s'|'s|'first|'а)) |                     # possessive words (english)
          (?:(?:О'|делл'|Н'|L'|d'|o'|о'|О')?[\d\w_]+(?:-+[\d\w_]+)*) |      # words
          (?:[{1}]) |                                                       # punctuation
          (?:.)                                                             # all the rest
        '''.format(r"|".join([r"(?:" + x + r")(?!\w)" for x in self.abbrs.keys()]), string.punctuation + "№«»…‰"), re.VERBOSE | re.UNICODE)

    def tokenize(self, text):
        """
        Split text into tokens.
        """
        return re.findall(self.regexp, text)

    def get_abbrs(self):
        """
        Return abbreviations dictionary
        """
        return self.abbrs    

if __name__ == "__main__":

    text = "Токенизация (tokenization, lexical analysis, графематический анализ, лексический анализ) - это когда в тексте выделяются слова, числа, и другие токены. Например, нахождение границ предложений. "  

    myTokenizer = Tokenizer()          
    tokens = myTokenizer.tokenize(text)
    print(text)
    for item in tokens:
        print("TOKEN:", item)
        
