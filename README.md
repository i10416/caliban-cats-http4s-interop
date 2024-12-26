# Caliban-Cats-Interop Starter-Kit

## About

Schema-first GraphQL Server Starter Kit based on Caliban and cats-effect using interop module

## Design

### Protocol

General purpose types, generated types and custom codecs for them

See protocol/src/main/graphql/schema.graphql

### Core

Core domain implementation

### Server

HTTP binding layer

## Build

```sh
sbt compile
```

## Run

```
sbt server/run
```

```sh
curl -X POST -H 'X-Token: mytoken' -d '{"query": "query { person(id:\"鈴木太郎\"){ id, fullName, articles { edges { node { title, authors { fullName, articles { edges { node { title } } } } } } } } }"}' localhost:8090/api/graphql
```

```json
{
  "data": {
    "person": {
      "id": "鈴木太郎",
      "fullName": "鈴木 太郎",
      "articles": {
        "edges": [
          {
            "node": {
              "title": "AIが変革する産業構造：2025年の労働市場予測",
              "authors": [
                {
                  "fullName": "鈴木 太郎",
                  "articles": {
                    "edges": [
                      {
                        "node": {
                          "title": "AIが変革する産業構造：2025年の労働市場予測"
                        }
                      },
                      {
                        "node": {
                          "title": "米中貿易戦争の次のステージ：新興市場への影響を読み解く"
                        }
                      }
                    ]
                  }
                },
                {
                  "fullName": "山田 花子",
                  "articles": {
                    "edges": [
                      {
                        "node": {
                          "title": "AIが変革する産業構造：2025年の労働市場予測"
                        }
                      },
                      {
                        "node": {
                          "title": "次世代インフラ投資：政府の新政策と民間企業の挑戦"
                        }
                      },
                      {
                        "node": {
                          "title": "米中貿易戦争の次のステージ：新興市場への影響を読み解く"
                        }
                      }
                    ]
                  }
                },
                {
                  "fullName": "佐藤 一",
                  "articles": {
                    "edges": [
                      {
                        "node": {
                          "title": "AIが変革する産業構造：2025年の労働市場予測"
                        }
                      },
                      {
                        "node": {
                          "title": "米中貿易戦争の次のステージ：新興市場への影響を読み解く"
                        }
                      }
                    ]
                  }
                }
              ]
            }
          },
          {
            "node": {
              "title": "米中貿易戦争の次のステージ：新興市場への影響を読み解く",
              "authors": [
                {
                  "fullName": "佐藤 一",
                  "articles": {
                    "edges": [
                      {
                        "node": {
                          "title": "AIが変革する産業構造：2025年の労働市場予測"
                        }
                      },
                      {
                        "node": {
                          "title": "米中貿易戦争の次のステージ：新興市場への影響を読み解く"
                        }
                      }
                    ]
                  }
                },
                {
                  "fullName": "鈴木 太郎",
                  "articles": {
                    "edges": [
                      {
                        "node": {
                          "title": "AIが変革する産業構造：2025年の労働市場予測"
                        }
                      },
                      {
                        "node": {
                          "title": "米中貿易戦争の次のステージ：新興市場への影響を読み解く"
                        }
                      }
                    ]
                  }
                },
                {
                  "fullName": "山田 花子",
                  "articles": {
                    "edges": [
                      {
                        "node": {
                          "title": "AIが変革する産業構造：2025年の労働市場予測"
                        }
                      },
                      {
                        "node": {
                          "title": "次世代インフラ投資：政府の新政策と民間企業の挑戦"
                        }
                      },
                      {
                        "node": {
                          "title": "米中貿易戦争の次のステージ：新興市場への影響を読み解く"
                        }
                      }
                    ]
                  }
                }
              ]
            }
          }
        ]
      }
    }
  }
}

```
