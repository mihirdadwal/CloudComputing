packer {
  required_plugins {
    amazon = {
      version = ">= 1.3.3"
      source  = "github.com/hashicorp/amazon"
    }
  }
}

source "amazon-ebs" "ubuntu" {
  region    = var.region
  vpc_id    = var.vpc_id
  subnet_id = var.subnet_id

  source_ami_filter {
    filters = {
      virtualization-type = var.virtualization_type
      name                = var.ami_name_filter
      root-device-type    = var.root_device_type
    }
    owners      = [var.ami_owner]
    most_recent = true
  }
  instance_type = var.instance_type
  ssh_username  = var.ssh_username
  ami_name      = "${var.ami_prefix}-${local.timestamp}"
  launch_block_device_mappings {
    device_name           = var.device_name
    volume_size           = var.volume_size
    volume_type           = var.volume_type
    delete_on_termination = var.delete_on_termination
  }
  disable_stop_instance = var.disable_stop_instance
  ami_users             = split(",", var.ami_users)
  ami_regions           = split(",", var.ami_regions)
  tags                  = var.tags
}