# IIPProductService
IIP Foundations Team product service


To Setup a ProductService Environment in AWS.
  create a VPC with cidr block of appropriate size
  create a subnet - using whole block is ok
  attach an internet gateway to the VPC
  enable dns hostnames on VPC
  setup a route table 0.0.0.0/0 and associate with subnet
  
  create an EC2 instance (t2.large with windows server 2012 base for example)
  100GB of storage should be sufficient
  create security group - RDP, HTTP, HTTPS
  
  create elastic ip address and associate with the ec2 instance
  optional - create domain and point at elastic ip address
  
Configure Machine to run software.
  git 1.9.5
  h2-2014-04-05
  jdk-8u51-nb-8_0_2-windows-x64.exe - netbeans isn't necessary but is my editor of choice and is useful for dev/test
  postgresql-9.4.4-3-windows-x64
  apache-maven-3.3.3-bin
  
  JAVA_HOME enviornment variable needs to be set for maven.
  
  Set a firewall rule for ports 80,443 to allow inbound traffic.
