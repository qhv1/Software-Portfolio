import os
from typing import Tuple

import pulumi
from pulumi import Output
from pulumi_command import local
from pulumi_aws import ec2
from sh import chmod, mv, rm, ssh_keygen, mkdir

import pulumi_export

KEYS_DIR: str = "./keys"


def generate_key_pair(key_name: str):
    if not os.path.exists(f"{KEYS_DIR}/{key_name}.pub") or not os.path.exists(f"{KEYS_DIR}/{key_name}.pem"):
        rm("-rf", f"{KEYS_DIR}/{key_name}.*")
        generate_ssh_key(key_name)

    pub_key_contents: str = ""
    with open(f"{KEYS_DIR}/{key_name}.pub", "r") as pub_key_file:
            pub_key_contents = pub_key_file.read()

    key_pair = ec2.KeyPair(
        resource_name="Generating SSH key",
        key_name=key_name,
        public_key=pub_key_contents,
    )

    pulumi.export(pulumi_export.key_pair_name, key_pair.key_name)
    return key_pair
    
def generate_ssh_key(key_name: str):
    mkdir("-p", KEYS_DIR)
    ssh_keygen("-m", "PEM", "-f", f"{KEYS_DIR}/{key_name}", "-N", "")
    mv(f"{KEYS_DIR}/{key_name}", f"{KEYS_DIR}/{key_name}.pem")
    chmod("400", f"{KEYS_DIR}/{key_name}.pem")

