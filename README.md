# commodities-auction

[![Clojars Project](https://img.shields.io/clojars/v/org.iiasa/commodities-auction.svg)](https://clojars.org/org.iiasa/commodities-auction)

A special case of generalized English auction (Gul & Stacchetti 2000) for differentiated commodity markets.

A global market for the homogeneous commodity consists of individual markets residing at different geographical locations. 
The markets are linked by the shared access to a finite pool of international suppliers. 
Suppliers differentiate price offers for distinct markets based on trade costs.
The algorithm finds the minimal Walrasian equilibrium (WE) for the matching problem in the exchange economy with indivisible objects, 
in which multiple objects (export supplies) are to be sold simultaneously, and each agent (market) wish to consume a bundle of different objects. 

Auction input parameters:
* Capacity by supplier
* Demand by market
* Trade cost by supplier and market - the floor price at which supplier has an incentive to sell commodity on a market

Assumptions:
* Minimal WE - suppliers are price-takers
* Inelastic demand - import goods can be substituted with domestic supply at higher prices
* Same slope of supply curves - a constant cost of adding one more unit of commodity to supplier’s market inventory (the same for internatinal and local suppliers)

Gul, F., & Stacchetti, E. (2000). The English Auction with Differentiated Commodities. Journal of Economic Theory, 92(1): 66-95. https://doi.org/10.1006/jeth.1999.2580


## Dependency Information

To include libraries add the following to your `:dependencies`:

### Leiningen
```clj
[org.iiasa/commodities-auction "0.2.2-SNAPSHOT"]
```

### Maven

```xml
<dependency>
  <groupId>org.iiasa</groupId>
  <artifactId>commodities-auction</artifactId>
  <version>0.2.2-SNAPSHOT</version>
</dependency>
```

## Documentation

* [API docs](https://shchipts.github.io/commodities-auction/)

## License

Copyright © 2020 International Institute for Applied Systems Analysis

Licensed under [MIT](http://opensource.org/licenses/MIT)