package main

import (
	"flag"
	"github.com/571899315/raptor/servicemesh-go/tcp"
	"runtime"
)



var host = flag.String("host", "", "host")
var port = flag.String("port", "3333", "port")



func main() {

	flag.Parse()
	runtime.GOMAXPROCS(runtime.NumCPU())




	tcpserver := new(tcp.TCPServer)






}
