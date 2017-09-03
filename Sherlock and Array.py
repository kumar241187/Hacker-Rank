#!/bin/python

import sys

def solve(a):
    # Complete this function
    size=len(a)
    j=size-1
    i,sum1,sum2=0,0,0
    while i < size and j>=0:
        if sum1==sum2 and ((i-j)==0):
            return "YES"
        elif sum1 < sum2 :
            sum1+=a[i]
            i=i+1
        else:
            sum2+=a[j]
            j=j-1
    return "NO"    
    
            
    
        

T = int(raw_input().strip())
for a0 in xrange(T):
    n = int(raw_input().strip())
    a = map(int, raw_input().strip().split(' '))
    result = solve(a)
    print(result)

