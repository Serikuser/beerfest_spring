https://github.com/Serikuser/beerfest

Same project but reworked with DI based on Spring Core and Spring JDBC.
DAO was reworked to work with JDBC Template provided by Spring JDBC. Recycling the DAO entailed the need to rework the connection pool.
Now the pool implements the DataSource interface, which allows you to use it as a connection source for the JDBC Template.
Connections are proxied through the ProxyConnection class, which allows cyclic use of pre-allocated connections.
The DI implementation allowed avoiding the use of lazy initialization via enam, as well as reducing the number of simultaneously existing objects, now commands receive services in the form of beans.
The main controller also receives commands in the form of beans. Thread safety when using different comands of shared resources is provided by processing each request in new thread, the possibility of this provides the use of the HttpServlet interface.

Work still in progress.
