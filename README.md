# hooks
React Hooks for ClojureScript

## Installation
### Add to `deps.edn` via Git deps
```clojure
{hooks/hooks {:git/url "git@github.com:roman01la/hooks.git"
              :sha "b0c80acb41570e5417eadcba3e5b63b1a8b8e725"}}
```
### Install NPM deps
```shell
yarn add react react-dom use-sync-external-store@1.0.0-beta-fdc1d617a-20211118 --save-dev
```

## Hooks

### `use-atom`
```clojure
(defonce num (atom 0)) ;; or any other Atom-like datatype

(defn button []
  (let [v (hooks.core/use-atom num)]
    [:button {:on-click #(swap! num inc)}
     v]))
```

### `use-subscribe` (re-frame)
This is just a shortcut for `(hooks.core/use-atom (rf/subscribe [:app/num]))`
```clojure
(def use-subscribe
  (hooks.core/create-use-subscribe rf/subscribe))

;; Why `create-use-subscribe`?
;; because you may have your own, enhanced `subscribe`

(defn button []
  (let [v (use-subscribe [:app/num])]
    [:button {:on-click #(rf/dispatch [:num/inc])}
     v]))
```
