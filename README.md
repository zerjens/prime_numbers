# prime_numbers

Simple Code Challenge, print out a matrix of primes and their products.

## To Run

### First run checks and tests

`lein do ancient, kibit, test`

This should print out that all dependencies are up to day, style checks
are valid, and that all tests pass.  You can optionally regenerate the
html formatted documentation with:

`lein marg`

### Generate Table of Prime Products

There are two algorithms to use, specify the alternative one by giving
the command line arg "alt", but specify the the table size when you do:

`lein trampoline run`</br>
 	2	3	5	7	11	13	17	19	23	29</br>
2	4	6	10	14	22	26	34	38	46	58</br>
3	6	9	15	21	33	39	51	57	69	87</br>
5	10	15	25	35	55	65	85	95	115	145</br>
7	14	21	35	49	77	91	119	133	161	203</br>
11	22	33	55	77	121	143	187	209	253	319</br>
13	26	39	65	91	143	169	221	247	299	377</br>
17	34	51	85	119	187	221	289	323	391	493</br>
19	38	57	95	133	209	247	323	361	437	551</br>
23	46	69	115	161	253	299	391	437	529	667</br>
29	58	87	145	203	319	377	493	551	667	841</br>

`lein trampoline run 5`</br>
 	2	3	5	7	11</br>
2	4	6	10	14	22</br>
3	6	9	15	21	33</br>
5	10	15	25	35	55</br>
7	14	21	35	49	77</br>
11	22	33	55	77	121</br>

`lein trampoline run 5 alt`</br>
 	2	3	5	7	11</br>
2	4	6	10	14	22</br>
3	6	9	15	21	33</br>
5	10	15	25	35	55</br>
7	14	21	35	49	77</br>
11	22	33	55	77	121</br>

