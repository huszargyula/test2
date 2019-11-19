<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.2" tiledversion="1.2.5" name="tileset" tilewidth="32" tileheight="32" spacing="2" tilecount="30" columns="30">
 <image source="map.png" width="1024" height="64"/>
 <tile id="0">
  <properties>
   <property name="type" value="BLUEGEM"/>
  </properties>
 </tile>
 <tile id="2">
  <properties>
   <property name="type" value="STARTLOCATION"/>
  </properties>
 </tile>
 <tile id="15">
  <properties>
   <property name="type" value="GNOMEOBJECT"/>
  </properties>
  <animation>
   <frame tileid="15" duration="170"/>
   <frame tileid="16" duration="170"/>
   <frame tileid="17" duration="170"/>
  </animation>
 </tile>
</tileset>
