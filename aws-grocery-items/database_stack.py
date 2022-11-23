import pulumi
from pulumi_aws import ec2
import pulumi_export
from pulumi_command import remote, local

UBUNTU_20_AMI = "ami-0149b2da6ceec4bb0"

def create_database_stack(config: pulumi.Config, network_stack: pulumi.StackReference):
    database_instance = ec2.Instance(
        resource_name="Creating Database instance",
        instance_type=config.require_object("database_instance")["instance_type"],
        ami=UBUNTU_20_AMI,
        root_block_device=ec2.InstanceRootBlockDeviceArgs(
            delete_on_termination=True,
            volume_size=config.require_object("database_instance")["instance_size"]
        ),
        subnet_id=network_stack.require_output(pulumi_export.private_subnet),
        tags={"Name": "grocery-item-database"},
        key_name=network_stack.require_output(pulumi_export.key_pair_name)
    )

    connection = remote.ConnectionArgs(
        host=network_stack.require_output(pulumi_export.bastion_instance_public_ip),
        user="ubuntu",
        private_key="./keys/aws-grocery-key.pem"
    )

    lcmd = local.Command(
        create=f"scp -i ./keys/aws-grocery-key.pem ./keys/aws-grocery-key.pem ubuntu"
    )

    cmd = remote.Command(
        connection=connection,
        create=f"ssh "
    )