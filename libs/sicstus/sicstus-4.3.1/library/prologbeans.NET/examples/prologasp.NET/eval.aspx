<%@ Page Language="C#" Debug="true" %>
<%@ Import Namespace="se.sics.prologbeans" %>

<html>
   <head><title>Evaluator</title></head>
   <body>
      <center>

      <!-- [PM] 4.1.3 SPRM 11820 -->       
      <h1>Security Warning</h1>
      <b>This ASPX example has a number of security vulnerabilites
      and is for illustrative purposes only. Consult with an expert.</b>

      <form>
         <h3> Expression: <input name="Expr" type=text representation="<%=Request.QueryString["Expr"]%>">
            <%
               PrologSession pSession = new PrologSession();
               String evQuery = Request.QueryString["Expr"];
               String output = "";
	       if (evQuery != null) {
		 Bindings bindings = new Bindings().bind("E",evQuery + '.');
                 session.connect();     /* This will connect if necessary. */
		 QueryAnswer answer = pSession.executeQuery("evaluate(E,R)", bindings);
		 PBTerm result = answer.getValue("R");

		 if (result != null) {
		   output = "<h4>Result = " + result + "</h4>";
		 } else {
		   output = "<h4>Error: " + answer.Error + "</h4>";
		 }
	       }
            %>
       </form>

       </center>

       <%= output  %><br>
       </font>
       <p><hr>Powered by SICStus Prolog

   </body>
</html>
