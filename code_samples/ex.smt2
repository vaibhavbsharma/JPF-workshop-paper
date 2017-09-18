(set-logic QF_BV)
(set-info :smt-lib-version 2.0)

; one variable per array entry
(declare-fun x0 () (_ BitVec 32))
(declare-fun x1 () (_ BitVec 32))
; a variable to represent 'sum'
(declare-fun sum () (_ BitVec 32))
; one 'sum' variable per loop iteration
(declare-fun sum0 () (_ BitVec 32))
(declare-fun sum1 () (_ BitVec 32))
; bound bitvectors
(assert (bvsge sum0 #x80000000))
(assert (bvsle sum0 #x7fffffff))
(assert (bvsge sum1 #x80000000))
(assert (bvsle sum1 #x7fffffff))
(assert (bvsge x0 #x80000000))
(assert (bvsle x0 #x7fffffff))
(assert (bvsge x1 #x80000000))
(assert (bvsle x1 #x7fffffff))
; unrolled lines 5, 6 in Listing 1
(assert (or (and (= x0 #x00000000) 
                 (= sum0 #x00000000)) 
            (or (and (bvsgt x0 #x00000000) 
                     (= sum0 #x00000001)) 
                (and (bvslt x0 #x00000000) 
                     (= sum0 #xffffffff)))))
; second iteration of unrolling lines 5, 6
(assert (or (and (= x1 #x00000000) 
                 (= sum1 #x00000000)) 
            (or (and (bvsgt x1 #x00000000) 
                     (= sum1 #x00000001)) 
                (and (bvslt x1 #x00000000) 
                     (= sum1 #xffffffff)))))
; merge function for 'sum' variable
(assert (= sum (bvadd sum0 sum1)))
; branch on line 9 of Listing 1
(assert (bvslt sum #x00000000))



(check-sat)
(get-model)
(exit)
