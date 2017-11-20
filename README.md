# kubernetes-deployment-testing

This project uses [Arquillian Kube Extension](http://arquillian.org/kube) and 
[Kubernetes Assertions](https://github.com/fabric8io/fabric8/tree/master/components/kubernetes-assertions) to test a sample 
kubernetes deployment for the 
[PHP Guestbook application with Redis](https://kubernetes.io/docs/tutorials/stateless-application/guestbook/).

Arquillian Kube extension takes a black box approach to testing and will neither mutate the containers (by deploying, 
reconfiguring etc) nor the Kubernetes resources.

This extension is used for Immutable Infrastructure and Integration Testing, wherein the test cases are meant to consume and 
test the provided services and assert that the environment is in the expected state.

**Pre-requisites to use kubernetes extension, is to have the kubernetes cluster running on your host machine.**

## Setup Kubernetes Cluster Locally
One of the easier way to setup and start the kubernetes cluter on your local host is to use 
[minikube](https://github.com/kubernetes/minikube).

## Project Dependencies
For kubernetes deployment testing using Arquillian Kube extension, the following dependencies should be included in pom.xml:

### Arquillian Cube BOM - Unified Dependencies
```java
<properties>
    <version.arquillian_cube>${latest_released_version}</version.arquillian_cube>
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.arquillian.cube</groupId>
            <artifactId>arquillian-cube-bom</artifactId>
            <version>${version.arquillian_cube}</version>
            <scope>import</scope>
            <type>pom</type>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### Arquillian Cube Requirement
```java
<dependency>
      <groupId>org.arquillian.cube</groupId>
      <artifactId>arquillian-cube-requirement</artifactId>
      <scope>test</scope>
</dependency>
```

### Arquillian Cube Kubernetes
```java
<dependency>
      <groupId>org.arquillian.cube</groupId>
      <artifactId>arquillian-cube-kubernetes</artifactId>
      <scope>test</scope>
</dependency>
```    

### Arquillian Junit
```java
<dependency>
      <groupId>org.jboss.arquillian.junit</groupId>
      <artifactId>arquillian-junit-standalone</artifactId>
      <version>${latest_released_version}</version>
      <scope>test</scope>
</dependency>
```

For kubernetes assertions, the following dependency should be added in the pom.xml

### Fabric8 Kubernetes Assertions 
```java
<dependency>
      <groupId>io.fabric8</groupId>
      <artifactId>kubernetes-assertions</artifactId>
      <version>${latest_released_version}</version>
      <scope>test</scope>
</dependency>
```        


