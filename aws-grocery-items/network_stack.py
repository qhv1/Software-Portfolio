import pulumi
from pulumi import Output
import pulumi_export
from pulumi_aws import ec2

UBUNTU_20_AMI = "ami-0149b2da6ceec4bb0"

def create_network_stack(config: pulumi.Config, key_pair: Output[str]):
    vpc = ec2.Vpc(
        resource_name="Creating VPC",
        cidr_block=config.require("cidr_block"),
        tags={"Name": "grocery-list-vpc"},
    )

    private_subnet = ec2.Subnet(
        resource_name="Creating private subnet",
        vpc_id=vpc.id,
        cidr_block=config.require("private_cidr_block"),
        tags={"Name": "private-subnet-grocery"}
    )

    public_subnet = ec2.Subnet(
        resource_name="Creating public subnet",
        vpc_id=vpc.id,
        cidr_block=config.require("public_cidr_block"),
        map_public_ip_on_launch=True,
        tags={"Name": "public-subnet-grocery"}
    )

    internet_gateway = ec2.InternetGateway(
        resource_name="Creating internet gateway",
        vpc_id=vpc.id,
        tags={"Name": "igw-grocery-list"}
    )

    nat_elastic_ip = ec2.Eip(
        resource_name="Creating elastic ip for NAT",
        tags={"Name": "nat-gateway-ip"}
    )

    nat_gateway = ec2.NatGateway(
        resource_name="Creating NAT gateway",
        subnet_id=public_subnet.id,
        allocation_id=nat_elastic_ip.id,
        opts=pulumi.ResourceOptions(depends_on=[nat_elastic_ip])
    )

    public_route_table = ec2.RouteTable(
        resource_name="Public Route Table for IGW",
        vpc_id=vpc.id,
        routes=[ec2.RouteTableRouteArgs(
            cidr_block="0.0.0.0/0",
            gateway_id=internet_gateway.id
        )],
        tags={"Name": "public-route-table"},
        opts=pulumi.ResourceOptions(depends_on=[internet_gateway])
    )

    private_route_table = ec2.RouteTable(
        resource_name="Private Route Table for NAT",
        vpc_id=vpc.id,
        routes=[ec2.RouteTableRouteArgs(
            cidr_block="0.0.0.0/0",
            gateway_id=nat_gateway.id
        )],
        tags={"Name": "private-route-table"},
        opts=pulumi.ResourceOptions(depends_on=[nat_gateway])
    )

    #private
    ec2.RouteTableAssociation(
        resource_name="Creating private route table assocation w/ private subnet",
        route_table_id=private_route_table.id,
        subnet_id=private_subnet.id,
        opts=pulumi.ResourceOptions(depends_on=[private_route_table, private_subnet])
    )

    #public
    ec2.RouteTableAssociation(
        resource_name="Creating public route table assocation w/ public subnet",
        route_table_id=public_route_table,
        subnet_id=public_subnet.id,
        opts=pulumi.ResourceOptions(depends_on=[public_route_table, public_subnet])
    )

    bastion_instance = ec2.Instance(
        resource_name="Creating Bastion ec2 instance",
        ami=UBUNTU_20_AMI,
        subnet_id=public_subnet.id,
        instance_type=config._require_object("bastion_instance")["instance_type"],
        root_block_device=ec2.InstanceRootBlockDeviceArgs(
            delete_on_termination=True,
            volume_size=config._require_object("bastion_instance")["instance_size"]
        ),
        key_name=(key_pair.key_name),
        tags={"Name": "grocery-item-bastion"},
        opts=pulumi.ResourceOptions(depends_on=[public_subnet, key_pair])
    )
    # TODO ADD SECURITY GROUP FOR SSH CONNECTION TO BASTION SERVER!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    # !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    # !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


    pulumi.export(pulumi_export.vpc, vpc.id)
    pulumi.export(pulumi_export.public_subnet, public_subnet.id)
    pulumi.export(pulumi_export.private_subnet, private_subnet.id)
    pulumi.export(pulumi_export.bastion_instance_public_ip, bastion_instance.public_ip)