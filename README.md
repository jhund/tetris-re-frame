# Tetris

A browser based Tetris game implemented in ClojureScript using the [re-frame](https://github.com/Day8/re-frame) application pattern.

[Play the demo](https://jhund.github.io/tetris-re-frame)


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

The app data is stored in Re-frame's `@re-frame.db/app-db`:

~~~
{
  :game {:active-block {:x 5
                        :y 7
                        :color-idx 5
                        :shape []}
         :done false
         :height 20
         :landed-blocks []
         :score 0
         :width 10}
  :ui    {:state :ideal} ; Currently not used, one of : :blank, :loading, :partial, :error, :ideal
}
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
