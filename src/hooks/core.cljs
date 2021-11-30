(ns hooks.core
  (:require [react]
            [hooks.impl]))

;; use-atom hook
(defn use-atom
  "Takes an Atom or Reagent's RAtom, or any other Atom-like ref type,
  subscribes UI component to changes in the ref
  and returns current state value of the ref"
  [ref]
  (let [subscribe (hooks.impl/use-batched-subscribe ref)
        get-snapshot (react/useCallback #(-deref ref) #js [ref])]
    (hooks.impl/use-sync-external-store subscribe get-snapshot)))
