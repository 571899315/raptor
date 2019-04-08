package cache

type Cache interface {
	Set(string, []byte) error

	Get(string) ([]byte, error)

	Del(string) error

	GetStat() Stat
}

func New(dataType string) Cache {
	var cache Cache

	if dataType == "inmemory" {
		cache = newInMemroyCache()
	}

	//if dataType == nil {
	//	//cache
	//}

	return cache
}
