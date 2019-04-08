package main

import (
	"github.com/571899315/raptor/raptorDb/cache"
	"github.com/571899315/raptor/raptorDb/server"
)

func main() {
	c := cache.New("inmemory")
	server.New(c).Listen()
}
