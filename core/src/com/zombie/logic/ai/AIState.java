package com.zombie.logic.ai;

public enum AIState {

	NO_TARGET, 	//��� ����
	FLEING,		//�������
	ATTACKING,	//�������
	INTEREST; 	//��������� ������� (� �����\��������� ���, ������� ������ ������)
	
	
	public boolean isPeaceful(){
		if (this ==NO_TARGET || this == INTEREST)
			return true;
		return false;
	}
}
