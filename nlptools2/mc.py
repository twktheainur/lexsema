#!/usr/bin/python

# Some Magic Constants for POS-Tagging

# Части речи
NOUN = "NOUN"
NPRO = "NPRO"
ADJF = "ADJF"
ADJS = "ADJS"
PRTF = "PRTF"
PRTS = "PRTS"
PREP = "PREP"
PRCL = "PRCL"
CONJ = "CONJ"
ADVB = "ADVB"
NUMB = "NUMB"
VERB = "VERB"
INFN = "INFN"
ROMN = "ROMN"
LATN = "LATN"

# Граммемы
_nomn = "nomn"
_gent = "gent"
_datv = "datv"
_accs = "accs"
_ablt = "ablt"
_loct = "loct"
_voct = "voct"
_gen2 = "gen2"
_acc2 = "acc2"
_loc2 = "loc2"

_masc = "masc"
_femn = "femn"
_ms_f = "Ms-f"
_neut = "neut"

_sing = "sing"
_plur = "plur"

_pres = "pres"
_past = "past"

_abbr = "Abbr"
_name = "Name"
_surn = "Surn"
_patr = "Patr"

_cases = {_nomn, _gent, _datv, _accs, _ablt, _loct, _voct, _gen2, _acc2, _loc2} # Падеж
_genders = {_masc, _femn, _ms_f, _neut} # Род
_declinable = {NOUN, NPRO, ADJF, PRTF}  # Склоняемые по падежам части речи
_btwn_prep_noun = {ADJF, PRTF, PRCL, CONJ, ADVB}    # Части речи, которые могут находиться между существительным и предлогом

# Внутреннее представление морф. парадигмы слова: [кот, {_lemma: "кот", _pos: "NOUN", _gram: {"anim","masc","sing","nomn"}]
_lemma = "norm"
_pos = "class"
_gram = "info"

BTAG = "<S>\n"  # Открывающий тэг предложения
ETAG = "</S>\n" # Закрывающий тэг предложения

NOPREP = "_"    # "Пустой" предлог
NOSUFF = "_"    # "Пустой" суффикс
