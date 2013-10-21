Serfnett and Friends
========

<p>
<b>Serfnett</b> is a servicing design with networking abstraction and dependancy-injection in mind.  A primary goal is to create scalable code that is designed well enough to not care whether it is a single-machine operation or multi-server, large-scale operation.
<br><br>I have chosen to embrace <a href="https://code.google.com/p/google-guice/">Guice</a> as a powerful medium to generalize the dependancies needed by any specific service (even if the dependancy is across a network) and satisfy the requirement via approaches experimented with in Veniredatum's RemoteInterfacer and Serfnett's ServiceInjector.
</p>

========

<p>
Projects currently involved in Serfnett development:
<ul>
<li><b><u>Serfnett</u>:</b> Provides basic tooling, extensions and SPI into a Serfnett service network using <a href="https://code.google.com/p/google-guice/">Guice DI</a> and <a href="https://code.google.com/p/guava-libraries/wiki/ServiceExplained">Guava's Service</a>.</li>
<li><b><u>Veniredatum</u>:</b> Provides <i>(experimental!)</i> RPC SPI tooled with <a href="https://code.google.com/p/guava-libraries/wiki/EventBusExplained">Guava's EventBus</a> and <a href="https://code.google.com/p/kryo/">Kryo</a>.</li>
<li><b><u>Ingamus</u>:</b> Provides an <i>(also experimental!)</i> (actor|behavior|event|service)-based SPI with tooled with Game Services and an Engine builder compatible with Serfnett.</li>
</ul>
</p>
