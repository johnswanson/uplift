# Uplift

This is a simple WebApp meant to track workouts. I'm basically using it as a
playground to experiment with Clojure and Clojurescript.

## Usage

Assuming you're set up with Leiningen, just clone the repo. Because this
is an experimental project, currently the only way to run the server is through
the REPL. To launch it, or to reload code after you change it, just run
`(reset)`. Note that if there's an error, you'll need to fix it and then run
`(refresh)` followed by `(reset)`.

I'm using Stuart Sierra's
[awesome](http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded)
"reloaded" workflow. **All state** exists within `uplift.system`. For example,
an instance of our storage protocol (currently just an in-memory store
persisting to disk every minute) is created and initialized, then passed to
the ring handler. This way, every `(refresh)` starts us out in a state we know
is valid.

## License

Copyright © 2013 John Swanson

Distributed under the Eclipse Public License, the same as Clojure.
