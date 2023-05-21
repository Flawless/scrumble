# TL;DR
Run service from the root project dir with:
```
bb tmux
```
and enjoy the service on http://localhost:8081

# About the project
One more project with a stupid name in my collection (:

This is test application to demonstrate Clojure usage on different field of web development. Note, that Clojure code can
have a good performance using low level things like Java arrays, type hinting, unchecked maths and so on (see
scrumble.scramble) for details. On the other side it can use high level abstraction (and most of the time it does) to
make code easier to write and read.

# Working with the project
This project doesn't have any build tools included, so the only way to use it is to launch the both clj and cljs REPLs.
Fortunatelly, I'm to lazy to manually launch each of them every time and to remember all required args, so you may use
babashka to start it with only one command:
```
bb tmux
```

Note, the tasks relies on CIDER profiles, that I have globally, so if you plan to work with REPLs with CIDER, it's
better to have in global deps.edn something like that:
```clj
{:aliases
 {:env/cider {:extra-deps {nrepl/nrepl {:mvn/version "1.0.0"}
						   cider/cider-nrepl {:mvn/version "0.30.0"}
						   refactor-nrepl/refactor-nrepl {:mvn/version "3.6.0"}
						   cider/piggieback {:mvn/version "0.5.2"}}}
  :cider/nrepl
  {:main-opts ["-m" "nrepl.cmdline" "--middleware" "[refactor-nrepl.middleware/wrap-refactor,cider.nrepl/cider-middleware,cider.piggieback/wrap-cljs-repl]"]}}}
```
You are free to ignore warnings about this aliases if you wish, the app has all required to operate dependencies
included in local deps.edn

Also, use
```
bb tasks
```
to see all available options, with brief descriptions.
