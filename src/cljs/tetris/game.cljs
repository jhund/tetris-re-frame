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
  ["#00FFFF"
   "#AA00FF"
   "#FFA500"
   "#0000FF"
   "#FF0000"
   "#00FF00"
   "#FFFF00"])

(def board-width 10) ; # of cells
(def board-height 20) ; # of cells
(def cell-size 20) ; in pixels

(defn transpose [matrix]
  (apply mapv vector matrix))

(defn flip [matrix]
  (vec (reverse matrix)))

(defn init-landed-blocks
  "Creates empty container for landed blocks (covers entire board)"
  [width height]
  (vec (repeat width (vec (repeat height -1)))))

(defn get-rand-block-shape []
  (transpose (rand-nth block-shapes)))

(defn start-new-block
  "Resets game-attrs to start a new active block."
  [game-attrs]
  (let [shape (get-rand-block-shape)]
    (assoc-in game-attrs
              [:active-block]
              {:x (- 5 (quot (count shape) 2))
               :y 0
               :color-idx (rand-int (count colors))
               :shape shape})))

(defn new-game
  "Returns game-attrs for a new game."
  []
  (start-new-block
    {:done false
     :landed-blocks (init-landed-blocks board-width board-height)
     :score 0}))

(defn no-collisions? [{:keys [done landed-blocks] {:keys [x y shape]} :active-block}]
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

(defn move-to-landed-blocks
  "Moves the active-block to landed-blocks."
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
  (if (no-collisions? game)
    game
    (assoc game :done true)))

(defn land-active-block [game]
  (-> game
      move-to-landed-blocks
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
  (update-in game
             [:active-block :shape]
             (comp transpose flip))) ; transpose and flip is equivalent to ccw rotation

(defn drop-to-ground [game]
  (land-active-block (last (take-while no-collisions? (iterate move-down game)))))

(def get-action-for-keycode
  { 37 move-left ; left arrow
    39 move-right ; right arrow
    38 rotate ; up arrow
    32 rotate ; space
    40 drop-to-ground}) ; down arrow

(defn apply-gravity [game-attrs]
  (let [new-game (move-down game-attrs)]
    (if (no-collisions? new-game)
      new-game
      (land-active-block game-attrs))))

(defn try-step [game-attrs f]
  (let [new-game (f game-attrs)]
    (if (no-collisions? new-game)
        new-game
        game-attrs)))
