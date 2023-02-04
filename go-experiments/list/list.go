package main

import ("container/list";"fmt")

func main() {
    l := list.New()
    l.PushBack(10)
    l.PushBack(12)
    l.PushBack(14)
    for e := l.Front(); e != nil; e = e.Next() {
        fmt.Println(e)
    }
}
