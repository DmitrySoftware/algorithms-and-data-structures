(defun binary (key arr from to)
    (if (>= from to)
	-1
    (let (mid value)
	(setf mid (ash (+ from to) -1))
	(setf value (nth mid arr))
	(if (< key value)
	    (binary key arr from mid)
	    (if (> key value)
		(binary key arr (+ mid 1) to)
		mid)))))

(defun binarySearch (key arr)
    (binary key arr 0 (length arr)))

(defun quickSort (arr)
    (runQuickSort arr 0 (length arr)))

(defun runQuickSort (arr from to)
    (let (m i j)
	(setf m (ash (+ from to) -1))
	(setf i from)
	(setf j to)
	(increment i arr m)
	(decrement j arr m)
	))

(defun increment (i arr m)
    (compareAndRun i arr m '> '+))


(defun decrement (i arr m)
    (compareAndRun i arr m '< '-))

(defun compareAndRun (i arr m compare action)
    (if (>= i (length arr)) (+ i 1) 
    (if (funcall compare (nth i arr) m)
	(increment (funcall action i 1) arr m)
	i)
    ))

