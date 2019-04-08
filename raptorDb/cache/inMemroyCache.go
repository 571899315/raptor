package cache

import "sync"

type inMemroyCache struct {
	inMemroyCache map[string][]byte
	lock          sync.RWMutex
	Stat
}

func (inMemroyCache *inMemroyCache) Set(k string, v []byte) error {
	inMemroyCache.lock.Lock()

	defer inMemroyCache.lock.Unlock()

	temp, exist := inMemroyCache.inMemroyCache[k]

	if exist {
		inMemroyCache.del(k, temp)
	}

	inMemroyCache.inMemroyCache[k] = v
	inMemroyCache.add(k, v)
	return nil
}

func (inMemroyCache *inMemroyCache) Get(k string) ([]byte, error) {
	inMemroyCache.lock.Lock()
	defer inMemroyCache.lock.Unlock()
	return inMemroyCache.inMemroyCache[k], nil

}

func (inMemroyCache *inMemroyCache) Del(k string) error {
	inMemroyCache.lock.Lock()
	defer inMemroyCache.lock.Unlock()

	temp, exist := inMemroyCache.inMemroyCache[k]

	if exist {
		delete(inMemroyCache.inMemroyCache, k)
		inMemroyCache.del(k, temp)
	}
	return nil

}

func (inMemroyCache *inMemroyCache) GetStat() Stat {

	return inMemroyCache.Stat

}

func newInMemroyCache() *inMemroyCache {
	return &inMemroyCache{make(map[string][]byte), sync.RWMutex{}, Stat{}}
}
