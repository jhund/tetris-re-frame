# Tetris

The browser based Tetris game implemented in ClojureScript using the [re-frame](https://github.com/Day8/re-frame) application pattern.


## Development Mode

### Run application:

```
lein clean
lein figwheel dev
```
Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).


## Production Build

```
lein clean
lein cljsbuild once min
```

## Data

Stored in `db`

~~~
{:ui    {:state :ideal ; :blank, :loading, :partial, :error, :ideal
         :message "All good"} 
 :game {:active-block {:x 5
                       :y 7 
                       :color-idx 5
                       :shape []}
        :done false
        :height 20
        :landed-blocks []
        :score 0
        :width 10}
~~~

## Game coordinate system

~~~
0 - - - - - - 10 (:game :width)
|              |
|    x-+       |
|    +-+       |
~              ~

~              ~
|              |
20 - - - - - - +
(:game :height)
~~~
