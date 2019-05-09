package tcp

import (
	"bufio"
	"fmt"
	"net"
	"os"
	"sync"
)

type TCPClient struct {
	/**
	连接信息
	*/
	connection net.Conn
	/**
	waitgroup
	*/
	waitgroup sync.WaitGroup
}

/**
获取连接
*/
func (client *TCPClient) connect(host string, port string) (net.Conn, error) {

	conn, err := net.Dial("tcp", host+":"+port)
	if err != nil {
		fmt.Println("Error connecting:", err)
		os.Exit(1)
	}
	defer conn.Close()
	client.connection = conn
	return conn, err
}

func (server *TCPClient) write(message string, conn net.Conn, wg *sync.WaitGroup) error {
	defer wg.Done()
	for {
		_, e := conn.Write([]byte(message + "\r\n"))
		if e != nil {
			fmt.Println("Error to send message because of ", e.Error())
			return e
		}
	}
	return nil

}

func (client *TCPClient) read(message string, conn net.Conn, wg *sync.WaitGroup) error {
	defer wg.Done()
	reader := bufio.NewReader(conn)
	for {
		line, err := reader.ReadString(byte('\n'))
		if err != nil {
			fmt.Print("Error to read message because of ", err)
			return err
		}
		fmt.Print(line)
	}
	return nil

}
