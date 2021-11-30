# hooks
React Hooks for ClojureScript

## Installation
### Add to `deps.edn` via Git deps
```clojure
{hooks/hooks {:git/url "git@github.com:roman01la/hooks.git"
              :sha "1a98408280892da1abebde206b5ca2444aced1b3"}}
```
### Install NPM deps
```shell
yarn add react react-dom use-sync-external-store@1.0.0-beta-fdc1d617a-20211118 --save-dev
```

## Hooks

### `hooks.core/use-atom`
```clojure
(defonce num (atom 0)) ;; or Reagent's RAtom or any other Atom-like datatype

(defn button []
  (let [v (hooks.core/use-atom num)]
    [:button {:on-click #(swap! num inc)}
     v]))
```

### `hooks.reagent/use-subscribe`
```clojure
(def use-subscribe
  (hooks.reagent/create-use-subscribe rf/subscribe))

;; Why `create-use-subscribe`?
;; because you may have your own, enhanced `subscribe`

(defn button []
  (let [v (use-subscribe [:app/num])]
    [:button {:on-click #(rf/dispatch [:num/inc])}
     v]))
```
