import pulumi
from pulumi import Output
from pulumi_aws import ec2

import pulumi_export
from database_stack import create_database_stack
from network_stack import create_network_stack
from ssh import generate_key_pair

UBUNTU_20_AMI = "ami-0149b2da6ceec4bb0"

config = pulumi.Config();
stack_type = config.require("type")

if stack_type == "network":
    key_pair: ec2.key_pair = generate_key_pair("aws-grocery-key")
    create_network_stack(config, key_pair)
elif stack_type == "database":
    network_stack = pulumi.StackReference("network-conf-stack")
    create_database_stack(config, network_stack)
