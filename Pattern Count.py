#!/bin/python

import sys
import re

def patternCount(s):
    # Complete this function
    return len(re.findall(("(?<=1)[0]+(?=1)"), s))

q = int(raw_input().strip())
for a0 in xrange(q):
    s = raw_input().strip()
    result = patternCount(s)
    print(result)

