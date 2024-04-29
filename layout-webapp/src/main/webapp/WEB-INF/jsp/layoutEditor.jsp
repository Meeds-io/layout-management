<%@page import="org.exoplatform.commons.api.settings.ExoFeatureService"%>
<%@page import="org.exoplatform.container.ExoContainerContext"%>
<%
  ExoFeatureService featureService = ExoContainerContext.getService(ExoFeatureService.class);
  boolean layoutAllAppsDrawerActive = featureService.isFeatureActiveForUser("LayoutAllAppsDrawer", request.getRemoteUser());
%>
<div class="VuetifyApp">
  <div id="layoutEditor">
    <script type="text/javascript">
      require(['PORTLET/layout/LayoutEditor'], app => app.init());
      eXo.env.portal.layoutAllAppsDrawer = <%=layoutAllAppsDrawerActive%>;
    </script>
  </div>
</div>
