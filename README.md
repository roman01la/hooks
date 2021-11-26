# hooks
React Hooks for ClojureScript

## Installation
### Add to `deps.edn` via Git deps
```clojure
{hooks/hooks {:git/url "git@github.com:roman01la/hooks.git"
              :sha "3a137bbe21782c210c2874aece012ac52a51b2e9"}}
```
### Install NPM deps
```shell
yarn add use-sync-external-store@1.0.0-beta-fdc1d617a-20211118 --save-dev
```

## Hooks

### `use-atom`
```clojure
(defonce num (atom 0)) ;; or any other Atom-like datatype

(defn button []
  (let [v (hooks/use-atom num)]
    [:button {:on-click #(swap! num inc)}
     v]))
```

### `use-subscribe` (re-frame)
This is just a shortcut for `(hooks/use-atom (rf/subscribe [:app/num]))`
```clojure
(def use-subscribe
  (hooks/create-use-subscribe rf/subscribe))

;; Why `create-use-subscribe`?
;; because you may have your own, enhanced `subscribe`

(defn button []
  (let [v (use-subscribe [:app/num])]
    [:button {:on-click #(rf/dispatch [:num/inc])}
     v]))
```
