# KeyCloak-SSO
#### 1.功能说明

keycloak的spi接口实现，该spi完成了对接postgre外部数据库，使用postgre数据库中的用户信息来进行登录验证，并给用户赋予角色，赋予自定义属性,并且可以自定义数据库连接url和表名（自定义配置）。 

关于如何配置，如下图所示：

![1660561602910](README-img/1660561602910.png)

#### 2，如何使用

项目克隆拉取下来之后，你只需要编译并且打包即可。但需要注意的是，不要忘记将postgresql的驱动一同打包，不然会找不到驱动。

#### 3，补充说明

在这个spi的实现你可以看到我们创建了一个新的role并且可以对用户的角色进行任意的授予。但大多数时候，你可能只需要授予一个default_role即可，因此你可以对其进行适当的修改。

#### 4，如何返回自己自定义的额外属性(字段)？

如图，我们在keycloak的控制台可以看到一些自定义额外的属性(例如 customattr，password ):

![1660559769272](README-img/1660559769272.png)

那么，这些自定义属性该如何获取呢，首先我们需要在实现spi的时候就要增加并设置这些属性，代码如下：

```java
UserModel local = session.userLocalStorage().getUserByUsername(username, realm);
if (local == null) {      
local.setSingleAttribute("customattr","cusValue");//就是在这里增加了额外的属性
}
```

我们能看到，代码的实现还是很简单的，但就是这样还是没法获取到这些自定义的属性的，只是在控制台中能够看到了。想要获取到这个自定义的属性，我们还需要对这些自定义的属性做映射，如何映射呢？

我们要找到我们服务自己对应的client下的Mappers选项卡，点击create，如图：

![1660560559582](README-img/1660560559582.png)

下方的两个红箭头是已经定义好了的两个映射属性，我们点击右上角的“create”，然后根据如下图所示进行设置。

![1660560942016](README-img/1660560942016.png)

最后，我们举一个在java中我们拿到token后获取自定义属性的例子来说：

```java
RefreshableKeycloakSecurityContext session= (RefreshableKeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        AccessToken token=session.getToken(); 
        Map<String,Object> claims=token.getOtherClaims(); 
        claims.get("customattr_showname");
```

最后说明一下，我们自定义的额外属性想要获取到，需要在client中将其映射出来，然后这些映射出来的属性都会保存在token中一个claims的map中，而getOtherClaims()则正是获取了其中的claims，因而能获取到我们自定义的属性名。