(defun prime(n)
    (if (<= n 0) '0
    (let (sqrt primes)
	(setf sqrt (floor (sqrt n)))
	(setf primes (make-array (+ sqrt 1) :initial-element 1 :element-type 'bit))
	(primeIter 2 sqrt primes)
	(primeDiv n 2 primes))))

(defun primeDiv(n i primes)
    (if (= i (length primes))
	'0
	(if (= (elt primes i) 0)
	    (primeDiv n (+ i 1) primes)
	    (if (= (mod n i) 0)
		i
		(primeDiv n (+ i 1) primes)))))

(defun primeIter(i sqrt primes)
    (setf i (ash i 1))
    (if (<= i sqrt)
	(primeMask i sqrt primes)))

(defun primeMask(i sqrt primes)
    (setf (elt primes i) 0)
    (primeIter i sqrt primes))
