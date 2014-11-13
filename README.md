# Connection pool for spymemcache

Spymemcache is single-threaded IO, it maintains only one single connection to memcache server even when we are doing multi-operation on this connection.
Spymemcache is fast, but in some cases we should maintain multi persistent connection to memcache server to do expensive jobs.

This project uses common pool 2.0 to create connection pool for spymemcache client. 


# Example

See Test package 
There's a singleton class that's used this connection pool.
From what I learn when using memcached, all recommend about using spymemcached and its options is included in that example, you should read.

# License

Do whatever you want.




