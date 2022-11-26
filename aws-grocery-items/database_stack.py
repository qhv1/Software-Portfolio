import pulumi
from pulumi_aws import ec2
import pulumi_export
from pulumi_command import remote, local

UBUNTU_20_AMI = "ami-0149b2da6ceec4bb0"

def create_database_stack(config: pulumi.Config, network_stack: pulumi.StackReference):
    database_security_group = ec2.SecurityGroup(
        resource_name="Creating security group for database server",
        vpc_id=network_stack.require_output(pulumi_export.vpc),
        name="database-security-group",
        ingress=[
            ec2.SecurityGroupIngressArgs(
                protocol="tcp",
                to_port="22",
                from_port="22",
                cidr_blocks=["10.0.0.0/8"]
            )
        ],
        egress=[
            ec2.SecurityGroupEgressArgs(
                protocol='-1',
                cidr_blocks=["0.0.0.0/0"],
                to_port=0,
                from_port=0
            )
        ],
    )


    database_instance = ec2.Instance(
        resource_name="Creating Database instance",
        instance_type=config.require_object("database_instance")["instance_type"],
        ami=UBUNTU_20_AMI,
        root_block_device=ec2.InstanceRootBlockDeviceArgs(
            delete_on_termination=True,
            volume_size=config.require_object("database_instance")["instance_size"]
        ),
        subnet_id=network_stack.require_output(pulumi_export.private_subnet),
        vpc_security_group_ids=[database_security_group.id],
        tags={"Name": "grocery-item-database"},
        key_name=network_stack.require_output(pulumi_export.key_pair_name),
        opts=pulumi.ResourceOptions(depends_on=[database_security_group])
    )
    

    # https://medium.com/devops-dudes/setting-up-an-ssh-agent-to-access-the-bastion-in-vpc-532918577949
    # setting up ssh-agent forwarding to bastion
    ssh_key = open("./keys/aws-grocery-key.pem", "r").read()
    connection = remote.ConnectionArgs(
        host=network_stack.require_output(pulumi_export.bastion_instance_public_ip),
        user="ubuntu",
        private_key=ssh_key
    )

    database_instance.private_ip.apply(lambda it: print(it))
    cmd = remote.Command(
        resource_name="Running command on database server",
        connection=connection,
        create=f"ssh -i ~/aws-grocery-key.pem ubuntu@{database_instance.private_ip.apply(lambda it: it)} 'touch testfile'",
        opts=pulumi.ResourceOptions(depends_on=[database_instance])
    )