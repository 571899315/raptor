package tcpserver

import (
	"bufio"
	"fmt"
	"github.com/571899315/raptor/raptorDb/cache"
	"io"
	"log"
	"net"
	"strconv"
	"strings"
)

type TcpServer struct {
	cache cache.Cache
}

func (server *TcpServer) Listen() {
	l, e := net.Listen("tcp", ":123456")

	if e != nil {
		panic(e)
	}

	for {
		c, e := l.Accept()

		if e != nil {
			panic(e)
		}

		go server.process(c)

	}

}

func (server *TcpServer) process(conn net.Conn) {

	defer conn.Close()

	r := bufio.NewReader(conn)

	for {

		op, e := r.ReadByte()

		if e != nil {
			if e != io.EOF {
				log.Print("close connection due to error:", e)
			}
			return
		}

		if op == 'S' {
			e = server.set(conn, r)
		}

	}

}

func (server *TcpServer) set(conn net.Conn, r *bufio.Reader) error {
	k, v, e := server.readKeyAndValue(r)

	if e != nil {
		return e
	}

	return sendResponse(nil, server.cache.Set(k, v), conn)

}

func (server *TcpServer) get(conn net.Conn, r *bufio.Reader) error {
	k, e := server.readKey(r)
	if e != nil {
		return e
	}
	v, e := server.cache.Get(k)

	return sendResponse(v, e, conn)

}

func (server *TcpServer) del(conn net.Conn, r *bufio.Reader) error {
	k, e := server.readKey(r)
	if e != nil {
		return e
	}
	return sendResponse(nil, server.cache.Del(k), conn)

}

func (server *TcpServer) readKey(r *bufio.Reader) (string, error) {
	klen, e := readLen(r)

	if e != nil {
		return "", e
	}

	k := make([]byte, klen)

	_, e = io.ReadFull(r, k)

	if e != nil {
		return "", e
	}

	return string(k), nil

}

func (server *TcpServer) readKeyAndValue(r *bufio.Reader) (string, []byte, error) {
	klen, e := readLen(r)

	if e != nil {
		return "", nil, e
	}

	vlen, e := readLen(r)

	if e != nil {
		return "", nil, e
	}

	k := make([]byte, klen)

	_, e = io.ReadFull(r, k)

	if e != nil {
		return "", nil, e
	}

	v := make([]byte, vlen)

	_, e = io.ReadFull(r, v)

	if e != nil {
		return "", nil, e
	}

	return string(k), v, nil

}

func sendResponse(value []byte, err error, conn net.Conn) error {

	if err != nil {
		errString := err.Error()

		tmp := fmt.Sprintf("-%d", len(errString)) + errString

		_, e := conn.Write([]byte(tmp))
		return e
	}

	vlen := fmt.Sprintf("-%d", len(value))

	_, e := conn.Write(append([]byte(vlen), value...))
	return e

}

func readLen(r *bufio.Reader) (int, error) {

	tmp, e := r.ReadString(' ')

	if e != nil {
		return 0, e
	}

	l, e := strconv.Atoi(strings.TrimSpace(tmp))
	if e != nil {
		return 0, e
	}
	return l, nil

}

func New(cache cache.Cache) *TcpServer {
	return &TcpServer{cache}
}
