#!/usr/bin/python
# -*- coding: utf-8 -*-

# Скрипт для сериализации абрревиатур или исключений для тэггера-лемматизатора (Python 3)

import os
import sys
from collections import defaultdict
from pickling import inner_func_str, dump_data

target = "abbr"

abbrfile = os.path.join(os.path.dirname(sys.argv[0]), "dicts/" + target + ".txt")

abbrs = inner_func_str()

with open(abbrfile, "r", encoding="UTF8") as fin:
    for line in fin:
        (word, norm, pos, grams) = line.strip().split("\t")
        abbrs[word]["norm"] = norm
        abbrs[word]["class"] = pos
        abbrs[word]["info"] = {grams}

dump_data(os.path.join(os.path.dirname(sys.argv[0]), "dicts/" + target + ".pkl"), abbrs)
