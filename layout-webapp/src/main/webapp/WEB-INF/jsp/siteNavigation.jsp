<%@page import="org.exoplatform.container.ExoContainerContext"%>
<%@page import="org.exoplatform.portal.config.UserACL"%>
<%@page import="org.exoplatform.portal.webui.portal.UIPortal"%>
<%@page import="org.exoplatform.portal.webui.util.NavigationUtils"%>
<%@page import="org.exoplatform.portal.webui.util.Util"%>
<%@page import="org.exoplatform.portal.mop.SiteKey"%>
<%@ page import="org.exoplatform.portal.mop.user.UserNavigation" %>
<%@ page import="io.meeds.layout.utils.SiteNavigationUtils" %>

<%
  UserACL userACL = ExoContainerContext.getService(UserACL.class);
  UIPortal currentUIPortal = Util.getUIPortal();
  SiteKey siteKey = Util.getUIPortal().getSiteKey();
  UserNavigation userNavigation = NavigationUtils.getUserNavigation(Util.getPortalRequestContext().getUserPortalConfig().getUserPortal(),
                                                                    siteKey);
  String siteName = userNavigation.getKey().getName();
  boolean isAdministrator = SiteNavigationUtils.isAdministrator();
  boolean canManageSiteNavigation = userNavigation.isModifiable() || userACL.hasEditPermissionOnPortal(siteKey.getTypeName(),
                                                                                                       siteKey.getName(),
                                                                                                       currentUIPortal.getEditPermission());
%>
<div class="VuetifyApp">
  <div id="siteNavigation">
    <script type="text/javascript">
      eXo.env.portal.siteKeyName = '<%=siteName%>';
      eXo.env.portal.isAdministrator = <%=isAdministrator%>;
      require(['PORTLET/layout/SiteNavigation'], app => app.init(<%=canManageSiteNavigation%>));
    </script>
  </div>
</div>
