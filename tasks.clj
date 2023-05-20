(ns tasks
  {:org.babashka/cli {:exec-args {:ns-data 1}}}
  (:require
   [babashka.process :refer [shell]]))

(defn tmux
  [& _args]
  (shell "tmux new-session -d 'clj -M:env/dev:env/cider:cider/nrepl'")
  (shell "tmux split-window -v 'shadow-cljs watch main'")
  (shell "tmux attach-session -d"))
