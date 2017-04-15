(set-env!
 :resource-paths #{"src"}
 :dependencies '[[org.clojure/core.async "0.3.442"]])

(task-options!
 pom {:project 'viebel/core-async-tuto
      :version "0.0.1"}
 jar {:manifest {"Foo" "Bar"}})

(deftask build
  "Build the project"
  []
  (comp (pom) (jar) (install)))
