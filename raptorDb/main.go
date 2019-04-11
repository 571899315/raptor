package main

import (
	"flag"
	"github.com/571899315/raptor/raptorDb/cache"
	"github.com/571899315/raptor/raptorDb/cluster"
	"github.com/571899315/raptor/raptorDb/http"
	"github.com/571899315/raptor/raptorDb/tcp"
	"log"
)

func main() {
	typ := flag.String("type", "inmemory", "cache type")
	ttl := flag.Int("ttl", 30, "cache time to live")
	node := flag.String("node", "127.0.0.1", "node address")
	clus := flag.String("cluster", "", "cluster address")
	flag.Parse()
	log.Println("type is", *typ)
	log.Println("ttl is", *ttl)
	log.Println("node is", *node)
	log.Println("cluster is", *clus)
	c := cache.New(*typ, *ttl)
	n, e := cluster.New(*node, *clus)
	if e != nil {
		panic(e)
	}
	go tcp.New(c, n).Listen()
	http.New(c, n).Listen()
}
