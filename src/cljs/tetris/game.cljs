(ns tetris.game)

(def block-shapes
  [[[0 0 0 0]  ; Bar
    [0 0 0 0]
    [1 1 1 1]
    [0 0 0 0]
    [0 0 0 0]]
   [[1 1]      ; Square
    [1 1]]
   [[1 0]      ; S
    [1 1]
    [0 1]]
   [[1 0]      ; L
    [1 0]
    [1 1]]
   [[1 1 1]    ; T
    [0 1 0]]])

(def colors
  ["#181818"
   "#585858"
   "#D8D8D8"
   "#AB4642"
   "#DC9656"
   "#F7CA88"
   "#A1B56C"
   "#86C1B9"
   "#7CAFC2"
   "#BA8BAF"
   "#A16946"])

(def board-width 10)
(def board-height 20)

(defn init-landed-blocks [width height]
  (vec (repeat width (vec (repeat height -1)))))

(defn transpose [matrix]
  (apply mapv vector matrix))

(defn flip [matrix]
  (vec (reverse matrix)))

(defn get-rand-block-shape []
  (transpose (rand-nth block-shapes)))

(defn start-new-block [game-attrs]
  (let [shape (get-rand-block-shape)]
    (assoc-in game-attrs
              [:active-block]
              {:x (- 5 (quot (count shape) 2))
               :y 0
               :color-idx (rand-int (count colors))
               :shape shape})))

(defn new-game
  []
  (start-new-block
    {:done false
     :landed-blocks (init-landed-blocks board-width board-height)
     :score 0}))

(defn valid-game? [{:keys [done landed-blocks] {:keys [x y shape]} :active-block}]
  (every? #{-1}
          (for [i (range (count shape))
                j (range (count (first shape)))
                :when (pos? (get-in shape [i j]))
                :let [matrix-x (+ x i)
                      matrix-y (+ y j)]]
            (get-in landed-blocks [matrix-x matrix-y]))))

(defn complete? [row]
  (not-any? #{-1} row))

(defn clear-completed-rows [{:as game-attrs :keys [landed-blocks]}]
  (let [remaining-rows (remove complete? (transpose landed-blocks))
        cc (- board-height (count remaining-rows))
        new-rows (repeat cc (vec (repeat board-width -1)))]
    (-> game-attrs
        (update-in [:score] inc)
        (update-in [:score] + (* 10 cc cc))
        (assoc :landed-blocks (transpose (concat new-rows remaining-rows))))))

(defn set-cell-in-landed-blocks [landed-blocks [x y color]]
  (assoc-in landed-blocks [x y] color))

(defn add-to-landed-blocks
  [{:as game-attrs
    :keys [done landed-blocks] {:keys [x y shape color-idx]} :active-block}]
  (let [shape-width (count shape)
        shape-height (count (first shape))]
    (assoc game-attrs :landed-blocks
           (reduce set-cell-in-landed-blocks landed-blocks
                   (for [i (range shape-width)
                         j (range shape-height)
                         :when (pos? (get-in shape [i j]))]
                     [(+ x i) (+ y j) color-idx])))))

(defn check-if-game-over [game]
  (if (valid-game? game)
    game
    (assoc game :done true)))

(defn landed [game]
  (-> game
      add-to-landed-blocks
      clear-completed-rows
      start-new-block
      check-if-game-over))

(defn move-down [game]
  (update-in game [:active-block :y] inc))

(defn move-left [game]
  (update-in game [:active-block :x] dec))

(defn move-right [game]
  (update-in game [:active-block :x] inc))

(defn rotate [game]
  (update-in game [:active-block :shape] (comp transpose flip)))

(defn drop-to-ground [game]
  (landed (last (take-while valid-game? (iterate move-down game)))))

(def codename
  { 37 "LEFT"
    38 "UP"
    39 "RIGHT"
    40 "DOWN"
    32 "SPACE"})

(def action
  { "LEFT" move-left
    "RIGHT" move-right
    "UP" rotate
    "SPACE" rotate
    "DOWN" drop-to-ground})

(defn apply-gravity [game-attrs]
  (let [new-game (move-down game-attrs)]
    (if (valid-game? new-game)
      new-game
      (landed game-attrs))))

(defn maybe-step [game-attrs f]
  (let [new-game (f game-attrs)]
    (if (valid-game? new-game)
        new-game
        game-attrs)))
