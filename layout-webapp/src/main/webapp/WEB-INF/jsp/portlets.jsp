<%@page import="org.exoplatform.container.ExoContainer"%>
<div class="VuetifyApp">
  <div id="portletsManagement">
    <script type="text/javascript">
      eXo.env.portal.ideEnabled = <%=ExoContainer.hasProfile("ide")%>;
      require(['PORTLET/layout/Portlets'], app => app.init());
    </script>
  </div>
</div>
