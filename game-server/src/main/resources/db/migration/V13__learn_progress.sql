-- Learn to Play course progress, per account. The client keeps the same JSON in localStorage for
-- guests; a signed-in user's copy lives here so "2 of 5 missions" follows them across devices. The
-- body is the client's `StoredProgress` JSON stored verbatim (a few hundred bytes at most): the
-- server never interprets it, the way `decks.data` and `cubes.data` are opaque to it.
ALTER TABLE users ADD COLUMN learn_progress TEXT;
