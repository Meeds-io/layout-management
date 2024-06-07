<%@page import="io.meeds.layout.service.LayoutAclService"%>
<%@page import="org.exoplatform.container.ExoContainerContext"%>
<%
  LayoutAclService aclService = ExoContainerContext.getService(LayoutAclService.class);
  boolean isAdministrator = aclService.isAdministrator(request.getRemoteUser());
%>
<div class="VuetifyApp">
  <div id="layoutEditor">
    <script type="text/javascript">
      require(['PORTLET/layout/LayoutEditor'], app => app.init());
      eXo.env.portal.isAdministrator = <%=isAdministrator%>;
    </script>
  </div>
</div>
