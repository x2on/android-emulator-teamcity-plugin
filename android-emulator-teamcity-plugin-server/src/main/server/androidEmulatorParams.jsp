<%--
 ~  Copyright 2010 Felix Schulze
 ~
 ~  Licensed under the Apache License, Version 2.0 (the "License");
 ~  you may not use this file except in compliance with the License.
 ~  You may obtain a copy of the License at
 ~
 ~      http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~  Unless required by applicable law or agreed to in writing, software
 ~  distributed under the License is distributed on an "AS IS" BASIS,
 ~  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~  See the License for the specific language governing permissions and
 ~  limitations under the License.
  --%>

<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<l:settingsGroup title="Runner Parameters">

    <tr>
        <th>
            <label for="avdName">AVD Name:</label><l:star/>
        </th>
        <td>
            <props:textProperty name="avdName" className="longField"/>
            <span class="error" id="error_avdName"></span>
        </td>
    </tr>

    <tr>
        <th>
            <label for="wipe">Reset emulator:</label>
        </th>
        <td>
            <props:checkboxProperty name="wipe"/>
        </td>
    </tr>

        <tr>
        <th>
            <label for="xwindow">Show emulator window:</label>
        </th>
        <td>
            <props:checkboxProperty name="xwindow"/>
        </td>
    </tr>

</l:settingsGroup>

